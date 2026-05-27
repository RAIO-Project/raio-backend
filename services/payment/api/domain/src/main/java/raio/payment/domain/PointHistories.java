package raio.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.payment.domain.type.PointHistoryType;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistories {

    /** 포인트 원장 식별자(PK) */
    private String id;

    /** 변동이 발생한 지갑 ID */
    private String walletId;

    /** 대상 사용자 ID */
    private String userId;

    /** 포인트 변동 유형 */
    private PointHistoryType type;

    /** 변동 금액. 충전/환불은 양수, 사용성 차감은 정책에 맞춰 음수 또는 양수로 기록 가능 */
    private Long amount;

    /** 변동 직후 잔액 스냅샷 */
    private Long balanceSnapshot;

    /** 원장 생성 일시 */
    private Instant createdAt;
}
