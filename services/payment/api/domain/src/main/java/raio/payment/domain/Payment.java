package raio.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.payment.domain.type.PaymentMethod;
import raio.payment.domain.type.PaymentStatus;
import raio.payment.domain.type.PgProvider;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    /** 결제 식별자(PK) */
    private String id;

    /** 가맹점 주문번호 (PG 콜백 역조회 키) */
    private String orderId;

    /** 결제 유저 ID */
    private String userId;

    /** 결제 금액 */
    private Long amount;

    /** 결제 상태 */
    private PaymentStatus status;

    /** 결제 수단 */
    private PaymentMethod method;

    /** PG사 식별자 */
    private PgProvider pgProvider;

    /** PG사 거래 번호 */
    private String externalTid;

    /** 실패사유 */
    private String failReason;
}
