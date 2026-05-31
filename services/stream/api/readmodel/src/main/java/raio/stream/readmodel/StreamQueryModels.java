package raio.stream.readmodel;

import lombok.Builder;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;

import java.time.Instant;

public final class StreamQueryModels {
    private StreamQueryModels() {
    }

    /** 목록 항목 (최신순/시청자순) */
    @Builder
    public record LiveStreamSummary(
            String id,
            String streamerId,
            String title,
            StreamCategory category,
            Integer maxViewerCount,
            StreamStatus status,
            Instant startedAt,
            Instant createdAt
    ) {
    }

    /** 시청자순 목록 항목. */
    @Builder
    public record LiveStreamRankItem(
            LiveStreamSummary stream,
            long currentViewerCount
    ) {
    }

    /** 단건 상세. */
    @Builder
    public record StreamDetail(
            String id,
            String streamerId,
            String title,
            StreamCategory category,
            StreamStatus status,
            Instant startedAt
    ) {
        public boolean isLive() {
            return status == StreamStatus.LIVE;
        }
    }
}
