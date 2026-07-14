package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.stream.application.port.StreamLiveRankQueryPort;
import raio.stream.application.port.StreamLiveRankQueryPort.LiveRank;
import raio.stream.application.port.StreamQueryPort;
import raio.stream.application.usecase.StreamReadByIdUseCase;
import raio.stream.application.usecase.StreamReadByStatusesUseCase;
import raio.stream.application.usecase.StreamReadByViewerUseCase;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamRankItem;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static raio.stream.exception.StreamErrorCode.STREAM_NOT_FOUND;

/**
 * 방송 조회 응용 서비스. 조회 유스케이스(최신순/시청자순/단건)를 함께 구현한다.
 * 라이프사이클(상태 전이)과 책임이 다르므로 {@link StreamLifecycleCommandService} 와 분리.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreamQueryService implements
        StreamReadByStatusesUseCase,
        StreamReadByViewerUseCase,
        StreamReadByIdUseCase {

    private final StreamQueryPort streamQueryPort;
    private final StreamLiveRankQueryPort streamLiveRankQueryPort;

    @Override
    public Page<LiveStreamSummary> findByStatuses(
            Set<StreamStatus> statuses,
            StreamCategory category,
            String query,
            Instant lastCreatedAt,
            int size
    ) {
        return streamQueryPort.findByStatusesAfter(statuses, category, query, lastCreatedAt, size);
    }

    @Override
    public Page<LiveStreamRankItem> findByViewer(
            StreamCategory category,
            String query,
            int offset,
            int size
    ) {
        int safeSize = Math.max(size, 1);
        int safeOffset = Math.max(offset, 0);

        List<LiveRank> ranks = streamLiveRankQueryPort.findLiveRanksByViewerDesc();
        if (ranks.isEmpty()) {
            return new PageImpl<>(List.of(), PageRequest.of(0, safeSize), 0);
        }

        List<Long> ids = ranks.stream().map(LiveRank::streamId).toList();
        Map<String, LiveStreamSummary> byId = streamQueryPort
                .findLiveSummariesByIds(ids, category, query)
                .stream()
                .collect(Collectors.toMap(LiveStreamSummary::id, Function.identity()));

        List<LiveStreamRankItem> ordered = ranks.stream()
                .map(rank -> {
                    LiveStreamSummary summary = byId.get(String.valueOf(rank.streamId()));
                    return summary == null ? null : new LiveStreamRankItem(summary, rank.viewerCount());
                })
                .filter(Objects::nonNull)
                .toList();

        int total = ordered.size();
        List<LiveStreamRankItem> content = ordered.stream()
                .skip(safeOffset)
                .limit(safeSize)
                .toList();

        return new PageImpl<>(content, PageRequest.of(safeOffset / safeSize, safeSize), total);
    }

    @Override
    public StreamDetail findById(String streamId) {
        return streamQueryPort.findDetailById(streamId)
                .orElseThrow(STREAM_NOT_FOUND::exception);
    }
}