package raio.settlement.readmodel;

import raio.settlement.domain.type.SettlementCycle;
import raio.settlement.domain.type.SettlementStatus;
import java.math.BigDecimal;
import java.time.Instant;

public final class SettlementReadModels {
    
    public record SettlementDetail(
            String id,
            String streamerId,
            SettlementCycle cycle,
            Instant periodStartAt,
            Instant periodEndAt,
            BigDecimal grossAmount,
            BigDecimal appliedFeeRate,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            SettlementStatus status,
            Instant createdAt
    ) {
    }
    
    public record SettlementSettingSummary(
            String streamerId,
            SettlementCycle currentCycle,
            SettlementCycle pendingCycle,
            Instant pendingCycleEffectiveAt,
            Instant nextSettlementAt,
            boolean active
    ) {
    }
}
