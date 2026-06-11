package raio.payment;

import raio.payment.domain.type.PaymentMethod;
import raio.payment.domain.type.PaymentStatus;
import raio.payment.domain.type.PgProvider;

import java.time.Instant;

public final class PaymentReadModels {

    private PaymentReadModels() {
    }

    /**
     * 결제 단건 조회 모델
     */
    public record PaymentDetail(
            String id,
            String orderId,
            String userId,
            Long amount,
            PaymentStatus status,
            PaymentMethod method,
            PgProvider pgProvider,
            String externalTid,
            String failReason,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    /**
     * 결제 목록 조회 모델
     */
    public record PaymentSummary(
            String id,
            String orderId,
            Long amount,
            PaymentStatus status,
            PaymentMethod method,
            PgProvider pgProvider,
            Instant createdAt
    ) {
    }
    
    /**
     * 결제 승인 조회 모델
     */
    public record PaymentConfirmResult(
            boolean success,
            String externalTid,
            String failMessage
    ) {
        public static PaymentConfirmResult success(String externalTid) {
            return new PaymentConfirmResult(true, externalTid, null);
        }
        
        public static PaymentConfirmResult failure(String failMessage) {
            return new PaymentConfirmResult(false, null, failMessage);
        }
    }
}
