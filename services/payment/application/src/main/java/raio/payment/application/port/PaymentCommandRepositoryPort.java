package raio.payment.application.port;

import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;

import java.util.Optional;
import java.util.function.Supplier;

public interface PaymentCommandRepositoryPort {
    
    <T> T transaction(Supplier<T> supplier);
    
    Payment save(Payment payment);

    Optional<Payment> updateStatus(String id, PaymentStatus status, String externalTid, String failReason);

    Optional<Payment> findByIdForUpdate(String id);
}
