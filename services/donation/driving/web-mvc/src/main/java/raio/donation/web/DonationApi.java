package raio.donation.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.donation.application.usecase.DonationCreateUseCase;
import raio.donation.application.usecase.DonationCreateUseCase.DonationCreateCommand;
import raio.jwt.principal.UserPrincipal;

import static raio.donation.exception.DonationErrorCode.DONATION_FORBIDDEN;

/**
 * 후원 REST 진입점.
 *
 * <p>후원자(senderId)·표시명(nickname) 모두 JWT 에서만 식별한다. 클라이언트가 보낸 값을 신뢰하면
 * 남의 포인트를 차감시키거나 표시명을 위조할 수 있으므로 요청 본문에서 받지 않는다.
 *
 * <p>{@link UserPrincipal} = (userId, nickname). REST 필터(JwtAuthenticationFilter)가 토큰에서 채워
 *  principal 로 심고, 여기서 {@code @AuthenticationPrincipal} 로 꺼낸다.
 *  (STOMP 측 StompPrincipal(userId, nickname) 과 대응)
 */
@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationApi {

    private final DonationCreateUseCase donationCreateUseCase;

    @PostMapping
    public ResponseEntity<DonationResponse> donate(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid DonationRequest request
    ) {
        if (principal == null) {
            throw DONATION_FORBIDDEN.exception(); // 비로그인 후원 거부
        }

        Long id = donationCreateUseCase.create(new DonationCreateCommand(
                request.streamId(),
                Long.parseLong(principal.userId()), // 신원은 토큰에서만
                request.receiverId(),
                request.amount(),
                request.message(),
                principal.nickname()                // 표시명도 토큰에서 (요청 본문 신뢰 X)
        ));
        return ResponseEntity.ok(new DonationResponse(id));
    }

    /** senderId·senderNickname 은 받지 않는다 — 후원자 신원·표시명 모두 토큰에서 온다. */
    public record DonationRequest(
            @NotNull Long streamId,
            @NotNull Long receiverId,
            @NotNull @Positive Long amount,
            @Size(max = 200) String message
    ) {
    }

    public record DonationResponse(Long donationId) {
    }
}