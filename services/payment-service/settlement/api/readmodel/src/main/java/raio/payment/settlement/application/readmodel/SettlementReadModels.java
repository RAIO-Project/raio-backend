package raio.payment.settlement.application.readmodel;

import raio.payment.settlement.domain.type.SettlementCycle;
import raio.payment.settlement.domain.type.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;

public final class SettlementReadModels {
    
    public record SettlementReadModel(
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
    
    public record SettlementSettingReadModel(
            String streamerId,
            SettlementCycle currentCycle,
            SettlementCycle pendingCycle,
            Instant pendingCycleEffectiveAt,
            boolean active
    ) {
    }
}
