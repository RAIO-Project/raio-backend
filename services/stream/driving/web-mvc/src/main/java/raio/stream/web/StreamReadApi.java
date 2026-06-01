package raio.stream.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raio.stream.application.usecase.StreamReadByIdUseCase;
import raio.stream.application.usecase.StreamReadByStatusesUseCase;
import raio.stream.application.usecase.StreamReadByViewerUseCase;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamRankItem;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;
import java.util.Set;

/**
 * 방송 조회 API.
 *  - 최신순:  GET /streams         (키셋, lastCreatedAt)
 *  - 시청자순: GET /streams/live    (Redis 랭킹, offset)
 *  - 단건상세: GET /streams/{id}    (소켓 진입 전 LIVE 검증 + 메타)
 */
@RestController
@RequestMapping("streams")
@RequiredArgsConstructor
public class StreamReadApi {

    private final StreamReadByStatusesUseCase streamReadByStatusesUseCase;
    private final StreamReadByViewerUseCase streamReadByViewerUseCase;
    private final StreamReadByIdUseCase streamReadByIdUseCase;

    @GetMapping
    public Page<LiveStreamSummary> getByStatuses(
            @RequestParam(defaultValue = "LIVE") Set<StreamStatus> statuses,
            @RequestParam(required = false) StreamCategory category,
            @RequestParam(required = false) String query,
            @RequestParam Instant lastCreatedAt,
            @RequestParam(defaultValue = "20") int size
    ) {
        return streamReadByStatusesUseCase.findByStatuses(statuses, category, query, lastCreatedAt, size);
    }

    @GetMapping("live")
    public Page<LiveStreamRankItem> getByViewer(
            @RequestParam(required = false) StreamCategory category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int size
    ) {
        return streamReadByViewerUseCase.findByViewer(category, query, offset, size);
    }

    @GetMapping("{streamId}")
    public StreamDetail getById(@PathVariable String streamId) {
        return streamReadByIdUseCase.findById(streamId);
    }
}
