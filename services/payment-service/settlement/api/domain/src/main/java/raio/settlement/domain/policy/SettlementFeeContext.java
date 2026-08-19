package raio.settlement.domain.policy;

import raio.settlement.domain.type.SettlementCycle;

import java.time.Instant;

public record SettlementFeeContext(
        String streamerId,
        SettlementCycle cycle,
        Instant settlementAt
) {
}
