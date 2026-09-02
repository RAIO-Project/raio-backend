package raio.payment.application.port;

import raio.payment.application.event.PaymentEvent;
import raio.payment.domain.PaymentOutbox;

import java.util.List;

public interface PaymentOutboxCommandRepositoryPort {
    
    PaymentOutbox save(PaymentEvent event);
    
    List<PaymentOutbox> findPendingEventList(int limit);
    
    void markPublished(String outboxId);
 
    void markRetry(String outboxId, String reason);
    
    void markFailed(String outboxId, String reason);
}
