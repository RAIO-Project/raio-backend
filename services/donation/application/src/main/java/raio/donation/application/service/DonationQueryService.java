package raio.donation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.donation.DonationReadModels.ReceivedDonation;
import raio.donation.application.port.DonationQueryPort;
import raio.donation.application.usecase.DonationReceivedReadUseCase;

import java.time.Instant;
import java.util.function.Consumer;

import static raio.donation.exception.DonationErrorCode.INVALID_DONATION_PERIOD;

/**
 * 스트리머 후원 수령 내역 조회.
 *
 * <p>결과를 모아 반환하지 않고 소비자에게 한 건씩 넘긴다. 배치처럼 기간 전체를 훑는 소비자가
 * 대상이라 결과가 수만 건이 될 수 있고, 이를 리스트로 모으면 서버 메모리가 건수에 비례해 늘어난다.
 *
 * <p>트랜잭션은 이 서비스가 아니라 어댑터가 연다. 커서 기반 조회는 순회가 끝날 때까지 커넥션이
 * 열려 있어야 하는데, 그 수명은 커서를 실제로 다루는 쪽에서 관리하는 것이 명확하기 때문이다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationQueryService implements DonationReceivedReadUseCase {

    private final DonationQueryPort donationQueryPort;

    @Override
    public void forEachReceivedDonation(
            Long streamerId,
            Instant periodStartAt,
            Instant periodEndAt,
            Consumer<ReceivedDonation> consumer
    ) {
        validatePeriod(periodStartAt, periodEndAt);

        donationQueryPort.forEachReceivedDonation(streamerId, periodStartAt, periodEndAt, consumer);
    }

    private void validatePeriod(Instant periodStartAt, Instant periodEndAt) {
        if (periodStartAt == null || periodEndAt == null || periodStartAt.isAfter(periodEndAt)) {
            throw INVALID_DONATION_PERIOD.exception();
        }
    }
}
