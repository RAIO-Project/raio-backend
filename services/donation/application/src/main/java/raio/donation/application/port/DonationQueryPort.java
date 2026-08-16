package raio.donation.application.port;

import raio.donation.DonationReadModels.ReceivedDonation;

import java.time.Instant;
import java.util.function.Consumer;

/** 후원 조회 포트 */
public interface DonationQueryPort {

    /**
     * 스트리머가 받은 정상 후원을 기간으로 조회해 한 건씩 전달 (최신순).
     *
     * <p>차단·환불된 건은 조회 제외
     *
     * @param consumer 조회된 후원 한 건을 처리할 소비자. 스트리밍 중 예외를 던지면 조회가 중단된다.
     */
    void forEachReceivedDonation(
            Long streamerId,
            Instant periodStartAt,
            Instant periodEndAt,
            Consumer<ReceivedDonation> consumer
    );
}
