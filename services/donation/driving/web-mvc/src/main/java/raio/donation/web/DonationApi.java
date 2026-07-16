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

import static raio.donation.exception.DonationErrorCode.DONATION_FORBIDDEN;

/**
 * 후원 REST 진입점.
 *
 * <p>후원자(senderId)는 JWT 에서만 식별한다. 클라이언트가 보낸 값을 신원으로 신뢰하면
 * 남의 포인트를 차감시킬 수 있으므로 요청 본문에서 받지 않는다.
 *
 * <p>TODO(nickname): senderNickname 은 표시용이라 아직 요청 본문으로 받는다.
 *  REST 필터(JwtAuthenticationFilter)가 principal 에 nickname 까지 담으면 토큰에서 추출하도록 교체한다.
 *  (STOMP 는 이미 StompPrincipal(userId, nickname) 방식)
 */
@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationApi {

    private final DonationCreateUseCase donationCreateUseCase;

    @PostMapping
    public ResponseEntity<DonationResponse> donate(
            @AuthenticationPrincipal String senderId,
            @RequestBody @Valid DonationRequest request
    ) {
        if (senderId == null) {
            throw DONATION_FORBIDDEN.exception(); // 비로그인 후원 거부
        }

        Long id = donationCreateUseCase.create(new DonationCreateCommand(
                request.streamId(),
                Long.parseLong(senderId), // 신원은 토큰에서만
                request.receiverId(),
                request.amount(),
                request.message(),
                request.senderNickname()
        ));
        return ResponseEntity.ok(new DonationResponse(id));
    }

    /** senderId 는 받지 않는다 — 토큰의 요청자가 후원자다. */
    public record DonationRequest(
            @NotNull Long streamId,
            @NotNull Long receiverId,
            @NotNull @Positive Long amount,
            @Size(max = 200) String message,
            @Size(max = 20) String senderNickname
    ) {
    }

    public record DonationResponse(Long donationId) {
    }
}