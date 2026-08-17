package raio.settlement.application.strategy;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import raio.settlement.domain.policy.SettlementFeeContext;
import raio.settlement.domain.policy.SettlementFeePolicy;

import java.math.BigDecimal;

@Component
@Order(4)
public class SettlementDefaultFeeStrategy implements SettlementFeePolicy {
    
    @Override
    public boolean supports(SettlementFeeContext context) {
        return context.cycle() != null;
    }
    
    @Override
    public BigDecimal resolveFeeRate(SettlementFeeContext context) {
        return switch (context.cycle()) {
            case DAILY -> new BigDecimal("0.15");
            case WEEKLY -> new BigDecimal("0.10");
            case MONTHLY -> new BigDecimal("0.05");
        };
    }
}
