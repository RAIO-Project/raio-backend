package raio.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.payment.domain.type.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {
    
    /**
     * 정산 식별자
     */
    private String id;
    
    /**
     * 대상 스트리머 ID
     */
    private String streamerId;
    
    /**
     * 총 후원금 (수수료 전)
     */
    private BigDecimal totalAmount;
    
    /**
     * 플랫폼 수수료
     */
    private BigDecimal feeAmount;
    
    /**
     * 실수령액 (total - fee)
     */
    private BigDecimal netAmount;
    
    /**
     * REQUESTED | COMPLETED
     */
    private SettlementStatus status;
    
    /**
     * 생성 일시
     */
    private Instant createdAt;
}