package raio.payment.application.event;

import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentEventType;
import raio.payment.domain.type.PaymentStatus;
import raio.payment.exception.PaymentException;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_INVALID_STATUS;

public sealed interface PaymentEvent {
    
    PaymentEventType eventType();
    
    String paymentId();
    
    // 결제 승인 이벤트
    record PaymentApprovedEvent(
            String paymentId,
            String orderId,
            String userId,
            Long amount
    ) implements PaymentEvent {
        
        public static PaymentApprovedEvent from(Payment payment) {
            if (payment.getStatus() != PaymentStatus.APPROVED)
                throw new PaymentException(PAYMENT_INVALID_STATUS);
            
            return new PaymentApprovedEvent(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getAmount());
        }
        
        @Override
        public PaymentEventType eventType() {
            return PaymentEventType.PAYMENT_APPROVED;
        }
    }
}
