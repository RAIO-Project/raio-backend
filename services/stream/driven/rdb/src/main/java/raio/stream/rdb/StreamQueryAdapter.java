package raio.stream.rdb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.stream.application.port.StreamQueryPort;
import raio.stream.domain.type.StreamStatus;
import raio.stream.rdb.entity.StreamsEntity;
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
 * 샘플 {@code ArticleQueryAdapter} 패턴 적용:
 *  - {@code QuerydslRepositorySupport} 상속
 *  - status enum 매핑은 {@code ConcurrentHashMap} 캐싱
 *  - 커서 페이지네이션 + {@code PageableExecutionUtils.getPage}
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
    public Page<LiveStreamSummary> findByStatusesAfter(Set<StreamStatus> statuses, Instant lastCreatedAt, int size) {
        var entityStatuses = statusMap.computeIfAbsent(
                statuses,
                (ignore) -> statuses.stream()
                        .map(StreamsEntityStatus::valueOf)
                        .collect(Collectors.toSet())
        );

        var query = getQuerydsl().createQuery()
                .select(streamsEntity)
                .from(streamsEntity)
                .where(
                        streamsEntity.createdAt.gt(lastCreatedAt),
                        streamsEntity.status.in(entityStatuses)
                )
                .orderBy(streamsEntity.createdAt.desc())
                .limit(size);
        var result = query.fetch();

        var totalCountQuery = getQuerydsl().createQuery()
                .select(streamsEntity.count())
                .from(streamsEntity)
                .where(
                        streamsEntity.createdAt.gt(lastCreatedAt),
                        streamsEntity.status.in(entityStatuses)
                );

        return PageableExecutionUtils.getPage(
                result.stream()
                        .map(streamsEntityMapper::toLiveStreamSummary)
                        .toList(),
                PageRequest.of(0, size),
                totalCountQuery::fetchOne
        );
    }
}
