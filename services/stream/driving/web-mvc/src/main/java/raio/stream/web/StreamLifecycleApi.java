package raio.stream.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.stream.application.usecase.StreamEndUseCase;
import raio.stream.application.usecase.StreamOpenUseCase;
import raio.stream.application.usecase.StreamStartUseCase;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;
import raio.stream.web.dto.StreamLifecycleDto.OpenStreamRequest;

/**
 * 방송 라이프사이클 API: 개설 → 시작 → 종료.
 */
@RestController
@RequestMapping("streams")
@RequiredArgsConstructor
public class StreamLifecycleApi {

    private final StreamOpenUseCase streamOpenUseCase;
    private final StreamStartUseCase streamStartUseCase;
    private final StreamEndUseCase streamEndUseCase;

    /** 방송 개설 (READY). */
    @PostMapping
    public StreamDetail open(@RequestBody OpenStreamRequest request) {
        return streamOpenUseCase.open(request.streamerId(), request.title(), request.category());
    }

    /** 방송 시작 (READY -> LIVE). */
    @PostMapping("{streamId}/start")
    public StreamDetail start(@PathVariable String streamId) {
        return streamStartUseCase.start(streamId);
    }

    /** 방송 종료 (LIVE -> ENDED). */
    @PostMapping("{streamId}/end")
    public StreamDetail end(@PathVariable String streamId) {
        return streamEndUseCase.end(streamId);
    }
}
