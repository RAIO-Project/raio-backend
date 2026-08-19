package raio.settlement.application.port;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 정산 대상 후원 내역을 조회하는 포트.
 *
 * <p>구현체는 정산 기간 내에 발생했으면서 아직 어떤 정산에도 포함되지 않은
 * 후원 건만 반환해야 한다. 이를 통해 정산 배치가 재실행되더라도
 * 동일 후원 건이 중복 정산되지 않는다.</p>
 */
public interface SettlementDonationQueryPort {

    List<SettlementDonationRevenue> findUnsettledDonations(
            String streamerId,
            Instant periodStartAt,
            Instant periodEndAt
    );

    record SettlementDonationRevenue(
            String donationId,
            BigDecimal grossAmount,
            Instant occurredAt
    ) {
    }
}