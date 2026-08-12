package raio.settlement.domain.policy;

import java.math.BigDecimal;

/**
 * 정산 수수료율을 결정하는 정책(Strategy)
 *
 * <p>
 * 수수료 정책은 운영 중 BO에서 변경되거나,
 * 스트리머별 계약·등급 등 다양한 기준으로 확장될 수 있으므로
 * Strategy Pattern을 적용하여 정책을 유연하게 교체할 수 있도록 설계
 * </p>
 */
public interface SettlementFeePolicy {
    
    BigDecimal resolveFeeRate(SettlementFeeContext context);
}





