package raio.payment.settlement.domain.policy;

import raio.payment.settlement.domain.type.SettlementCycle;

import java.time.Instant;

public record SettlementFeeContext(
        String streamerId,
        SettlementCycle cycle,
        Instant settlementAt
) {
}
