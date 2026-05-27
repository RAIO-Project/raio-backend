package raio.stream.rdb;

import com.querydsl.core.BooleanBuilder;
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
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static raio.stream.rdb.entity.QStreamsEntity.streamsEntity;

/**
 *  - category, query 동적 where 절을 {@link BooleanBuilder} 로 구성.
 *  - title 검색은 {@code containsIgnoreCase} (PostgreSQL ILIKE).
 */
@Repository
public class StreamQueryAdapter extends QuerydslRepositorySupport implements StreamQueryPort {

    private final StreamsEntityMapper streamsEntityMapper;
    private final Map<Set<StreamStatus>, Set<StreamsEntityStatus>> statusMap = new ConcurrentHashMap<>();

    public StreamQueryAdapter(StreamsEntityMapper streamsEntityMapper) {
        super(StreamsEntity.class);
        this.streamsEntityMapper = streamsEntityMapper;
    }

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

        var where = new BooleanBuilder()
                .and(streamsEntity.createdAt.gt(lastCreatedAt))
                .and(streamsEntity.status.in(entityStatuses));

        if (category != null) {
            where.and(streamsEntity.category.eq(StreamsEntityCategory.valueOf(category)));
        }
        if (query != null && !query.isBlank()) {
            where.and(streamsEntity.title.containsIgnoreCase(query));
        }

        var dataQuery = getQuerydsl().createQuery()
                .select(streamsEntity)
                .from(streamsEntity)
                .where(where)
                .orderBy(streamsEntity.createdAt.desc())
                .limit(size);
        var result = dataQuery.fetch();

        var totalCountQuery = getQuerydsl().createQuery()
                .select(streamsEntity.count())
                .from(streamsEntity)
                .where(where);

        return PageableExecutionUtils.getPage(
                result.stream()
                        .map(streamsEntityMapper::toLiveStreamSummary)
                        .toList(),
                PageRequest.of(0, size),
                totalCountQuery::fetchOne
        );
    }
}
