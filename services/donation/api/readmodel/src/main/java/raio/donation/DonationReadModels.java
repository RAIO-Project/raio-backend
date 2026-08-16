package raio.donation;

import lombok.Builder;

import java.time.Instant;

public final class DonationReadModels {

    private DonationReadModels() {
    }

    /** 스트리머가 받은 후원 모델. */
    @Builder
    public record ReceivedDonation(
            Long donationId,
            Long streamId,
            Long senderId,
            Long amount,
            String message,
            Instant donatedAt
    ) {
    }
}
