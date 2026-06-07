package raio.payment.application.port;

import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;

import java.util.Optional;

public interface PaymentCommandRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> updateStatus(String id, PaymentStatus status, String externalTid, String failReason);
}
