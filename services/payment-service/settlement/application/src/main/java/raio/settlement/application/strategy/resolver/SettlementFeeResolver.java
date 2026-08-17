package raio.settlement.application.strategy.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raio.settlement.domain.policy.SettlementFeeContext;
import raio.settlement.domain.policy.SettlementFeePolicy;

import java.math.BigDecimal;
import java.util.List;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_INVALID_FEE_RATE;

@Service
@RequiredArgsConstructor
public class SettlementFeeResolver {
    
    private final List<SettlementFeePolicy> polices;
    
    public BigDecimal resolve(SettlementFeeContext context) {
        return polices.stream()
                .filter(policy -> policy.supports(context))
                .findFirst()
                .orElseThrow(SETTLEMENT_INVALID_FEE_RATE::exception)
                .resolveFeeRate(context);
    }
}
