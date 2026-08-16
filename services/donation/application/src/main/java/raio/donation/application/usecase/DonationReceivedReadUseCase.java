package raio.donation.application.usecase;

import raio.donation.DonationReadModels.ReceivedDonation;

import java.time.Instant;
import java.util.function.Consumer;

public interface DonationReceivedReadUseCase {

    /**
     * 스트리머가 받은 후원 내역을 기간으로 조회해 한 건씩 소비자에게 넘긴다. 차단·환불 건은 제외.
     *
     * @param periodStartAt 조회 시작 시각 (포함)
     * @param periodEndAt   조회 종료 시각 (포함)
     * @param consumer      후원 한 건을 처리할 소비자
     */
    void forEachReceivedDonation(
            Long streamerId,
            Instant periodStartAt,
            Instant periodEndAt,
            Consumer<ReceivedDonation> consumer
    );
}
