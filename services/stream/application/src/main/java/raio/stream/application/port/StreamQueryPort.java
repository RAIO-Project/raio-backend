package raio.stream.application.port;

import org.springframework.data.domain.Page;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Set;

public interface StreamQueryPort {
    Page<LiveStreamSummary> findByStatusesAfter(Set<StreamStatus> statuses, Instant lastCreatedAt, int size);
}
