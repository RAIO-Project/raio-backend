package raio.stream.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raio.stream.application.usecase.StreamReadByStatusesUseCase;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Set;

/**
 *  - 페이지네이션: 커서 (lastCreatedAt + size).
 *  - 응답: {@code Page<LiveStreamSummary>} readmodel 직접 반환.
 */
@RestController
@RequestMapping("streams")
@RequiredArgsConstructor
public class StreamQueryApi {

    private final StreamReadByStatusesUseCase streamReadByStatusesUseCase;

    @GetMapping
    public Page<LiveStreamSummary> getStreamsByStatuses(
            @RequestParam(defaultValue = "LIVE") Set<StreamStatus> statuses,
            @RequestParam Instant lastCreatedAt,
            @RequestParam(defaultValue = "20") int size
    ) {
        return streamReadByStatusesUseCase.findByStatuses(statuses, lastCreatedAt, size);
    }
}
