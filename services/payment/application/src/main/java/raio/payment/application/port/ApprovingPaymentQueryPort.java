package raio.payment.application.port;

import raio.payment.domain.payment.Payment;

import java.util.List;

public interface ApprovingPaymentQueryPort {

    List<Payment> findApprovingWithPaymentKey();
}
