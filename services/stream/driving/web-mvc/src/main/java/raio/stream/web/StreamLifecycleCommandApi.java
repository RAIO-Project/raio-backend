package raio.stream.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import raio.jwt.principal.UserPrincipal;

import static raio.stream.exception.StreamErrorCode.STREAM_UNAUTHORIZED;

/**
 * 방송 라이프사이클 API: 개설 → 시작 → 종료.
 *
 * <p>요청자 식별은 JWT 에서만 한다({@code @AuthenticationPrincipal} = userId).
 * 클라이언트가 보낸 값을 신원으로 신뢰하지 않는다.
 */
@RestController
@RequestMapping("streams")
@RequiredArgsConstructor
public class StreamLifecycleCommandApi {

    private final StreamOpenUseCase streamOpenUseCase;
    private final StreamStartUseCase streamStartUseCase;
    private final StreamEndUseCase streamEndUseCase;

    /** 방송 개설 (READY). 개설자는 인증된 요청자 본인. */
    @PostMapping
    public StreamDetail open(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody OpenStreamRequest request
    ) {
        return streamOpenUseCase.open(requireLogin(principal), request.title(), request.category());
    }

    /** 방송 시작 (READY -> LIVE). 방송 주인만 가능. */
    @PostMapping("{streamId}/start")
    public StreamDetail start(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String streamId
    ) {
        return streamStartUseCase.start(streamId, requireLogin(principal));
    }

    /** 방송 종료 (LIVE -> ENDED). 방송 주인만 가능. */
    @PostMapping("{streamId}/end")
    public StreamDetail end(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String streamId
    ) {
        return streamEndUseCase.end(streamId, requireLogin(principal));
    }

    /** 토큰이 없으면 principal 이 null 이다. */
    private String requireLogin(UserPrincipal principal) {
        if (principal == null) {
            throw STREAM_UNAUTHORIZED.exception();
        }
        return principal.userId();
    }
}