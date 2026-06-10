package raio.donation.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.donation.application.usecase.DonationCreateUseCase;
import raio.donation.application.usecase.DonationCreateUseCase.DonationCreateCommand;

/**
 * 후원 REST 진입점.
 *
 * <p>현재 인증(JWT) 미연동이라 senderId/senderNickname 을 요청 본문으로 받는다.
 * TODO: 인증 붙이면 senderId/senderNickname 은 토큰에서 추출하고 요청에서 제거.
 */
@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationApi {

    private final DonationCreateUseCase donationCreateUseCase;

    @PostMapping
    public ResponseEntity<DonationResponse> donate(@RequestBody @Valid DonationRequest request) {
        Long id = donationCreateUseCase.create(new DonationCreateCommand(
                request.streamId(),
                request.senderId(),
                request.receiverId(),
                request.amount(),
                request.message(),
                request.senderNickname()
        ));
        return ResponseEntity.ok(new DonationResponse(id));
    }

    public record DonationRequest(
            @NotNull Long streamId,
            @NotNull Long senderId,      // TODO(auth): 토큰에서
            Long receiverId,
            @NotNull @Positive Long amount,
            @Size(max = 200) String message,
            String senderNickname        // TODO(auth): 토큰에서
    ) {}

    public record DonationResponse(Long donationId) {}
}