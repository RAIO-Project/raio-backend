package raio.payment.domain.settlement.policy;

import raio.payment.domain.settlement.type.SettlementCycle;

import java.time.Instant;

public record SettlementFeeContext(
        String streamerId,
        SettlementCycle cycle,
        Instant settlementAt
) {
}
