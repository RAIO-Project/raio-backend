package raio.stream.rdb;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.stream.application.port.StreamQueryPort;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.rdb.entity.StreamsEntity;
import raio.stream.rdb.entity.type.StreamsEntityCategory;
import raio.stream.rdb.entity.type.StreamsEntityStatus;
import raio.stream.rdb.mapper.StreamsEntityMapper;
import raio.stream.rdb.repository.StreamsJpaRepository;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static raio.stream.rdb.entity.QStreamsEntity.streamsEntity;

/**
 * 방송 조회 어댑터.
 */
@Repository
public class StreamQueryAdapter extends QuerydslRepositorySupport implements StreamQueryPort {

    private final StreamsEntityMapper streamsEntityMapper;
    private final StreamsJpaRepository streamsJpaRepository;
    private final Map<Set<StreamStatus>, Set<StreamsEntityStatus>> statusMap = new ConcurrentHashMap<>();

    public StreamQueryAdapter(StreamsEntityMapper streamsEntityMapper,
                              StreamsJpaRepository streamsJpaRepository) {
        super(StreamsEntity.class);
        this.streamsEntityMapper = streamsEntityMapper;
        this.streamsJpaRepository = streamsJpaRepository;
    }

    // ===== [최신순] 키셋 페이징 =====
    @Override
    public Page<LiveStreamSummary> findByStatusesAfter(
            Set<StreamStatus> statuses,
            StreamCategory category,
            String query,
            Instant lastCreatedAt,
            int size
    ) {
        var entityStatuses = statusMap.computeIfAbsent(
                statuses,
                (ignore) -> statuses.stream()
                        .map(StreamsEntityStatus::valueOf)
                        .collect(Collectors.toSet())
        );

        Predicate[] conditions = {
                streamsEntity.createdAt.gt(lastCreatedAt),
                statusIn(entityStatuses),
                categoryEq(category),
                titleContains(query)
        };

        var result = getQuerydsl().createQuery()
                .select(streamsEntity)
                .from(streamsEntity)
                .where(conditions)
                .orderBy(streamsEntity.createdAt.desc())
                .limit(size)
                .fetch();

        var totalCountQuery = getQuerydsl().createQuery()
                .select(streamsEntity.count())
                .from(streamsEntity)
                .where(conditions);

        return PageableExecutionUtils.getPage(
                result.stream().map(streamsEntityMapper::toLiveStreamSummary).toList(),
                PageRequest.of(0, size),
                totalCountQuery::fetchOne
        );
    }

    // ===== [시청자순] ID hydration =====
    @Override
    public List<LiveStreamSummary> findLiveSummariesByIds(
            Collection<Long> ids,
            StreamCategory category,
            String query
    ) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        Predicate[] conditions = {
                streamsEntity.id.in(ids),
                statusEq(StreamsEntityStatus.LIVE),  // Redis-DB 순간 불일치 안전망
                categoryEq(category),
                titleContains(query)
        };

        return getQuerydsl().createQuery()
                .select(streamsEntity)
                .from(streamsEntity)
                .where(conditions)
                .fetch()
                .stream()
                .map(streamsEntityMapper::toLiveStreamSummary)
                .toList();
    }

    // ===== 단건 상세 (PK 룩업) =====
    @Override
    public Optional<StreamDetail> findDetailById(String id) {
        return streamsJpaRepository.findById(Long.parseLong(id))
                .map(streamsEntityMapper::toStreamDetail);
    }

    // ===== 동적 조건 (null 이면 where 에서 무시) =====
    private BooleanExpression statusIn(Set<StreamsEntityStatus> statuses) {
        return streamsEntity.status.in(statuses);
    }

    private BooleanExpression statusEq(StreamsEntityStatus status) {
        return streamsEntity.status.eq(status);
    }

    private BooleanExpression categoryEq(StreamCategory category) {
        return category == null
                ? null
                : streamsEntity.category.eq(StreamsEntityCategory.valueOf(category));
    }

    private BooleanExpression titleContains(String query) {
        return (query == null || query.isBlank())
                ? null
                : streamsEntity.title.containsIgnoreCase(query);
    }
}