package raio.stream.application.usecase;

import org.springframework.data.domain.Page;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Set;

public interface StreamReadByStatusesUseCase {
    Page<LiveStreamSummary> findByStatuses(
            Set<StreamStatus> statuses,
            StreamCategory category,
            String query,
            Instant lastCreatedAt,
            int size
    );
}
