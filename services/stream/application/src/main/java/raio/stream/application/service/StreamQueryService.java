package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import raio.stream.application.port.StreamQueryPort;
import raio.stream.application.usecase.StreamReadByStatusesUseCase;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StreamQueryService implements StreamReadByStatusesUseCase {
    private final StreamQueryPort streamQueryPort;

    @Override
    public Page<LiveStreamSummary> findByStatuses(Set<StreamStatus> statuses, Instant lastCreatedAt, int size) {
        return streamQueryPort.findByStatusesAfter(statuses, lastCreatedAt, size);
    }
}
