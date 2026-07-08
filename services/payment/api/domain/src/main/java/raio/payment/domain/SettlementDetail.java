package raio.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDetail {
    
    /**
     * 상세 식별자
     */
    private String id;
    
    /**
     * 부모 정산 ID
     */
    private String settlementId;
    
    /**
     * 원장 ID (중복 집계 방지)
     */
    private String historyId;
    
    /**
     * 해당 건 금액
     */
    private BigDecimal amount;
    
    /**
     * 생성 일시
     */
    private Instant createdAt;
}