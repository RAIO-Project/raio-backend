package raio.stream.readmodel;

import lombok.Builder;
import raio.stream.domain.type.StreamStatus;

import java.time.Instant;

public final class StreamQueryModels {
    private StreamQueryModels() {
    }

    @Builder
    public record LiveStreamSummary(
            Long id,
            Long streamerId,
            String title,
            String category,
            Integer maxViewerCount,
            StreamStatus status,
            Instant startedAt,
            Instant createdAt
    ) {
    }
}
