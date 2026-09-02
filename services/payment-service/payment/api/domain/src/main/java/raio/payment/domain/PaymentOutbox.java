package raio.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import raio.payment.domain.type.PaymentEventType;
import raio.payment.domain.type.PaymentOutboxStatus;

/**
 * Payment Transactional Outbox 레코드.
 *
 * <p>결제 상태 변경과 같은 트랜잭션으로 INSERT 되어 커밋을 보장받고,
 * 별도 릴레이가 PENDING 레코드를 폴링해 브로커로 발행한다.
 * 발행은 at-least-once 이므로 컨슈머는 {@code id} 를 멱등키로 사용해야 한다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOutbox {
    
    /** Outbox 식별자(PK). 컨슈머 중복 제거(멱등) 키로도 쓰인다. */
    private String id;
    
    /** 이벤트를 발생시킨 (paymentId)  */
    private String paymentId;
    
    /** 이벤트 타입 */
    private PaymentEventType eventType;
    
    /** 이벤트 본문 (JSON) */
    private String payload;
    
    /** 발행 상태 */
    private PaymentOutboxStatus status;
    
    /** 발행 실패 횟수 */
    private int retryCount;
    
    /** 마지막 발행 실패 사유 */
    private String lastError;
    
    /**  재시도 상한에 도달 시 발행 포기 판단 정책 */
    public boolean isExhausted(int maxAttempts) {
        return retryCount + 1 >= maxAttempts;
    }
}
