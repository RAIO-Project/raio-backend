package raio.payment.application.port;

import raio.payment.domain.Payment;

import java.util.List;

public interface ApprovingPaymentQueryPort {

    List<Payment> findApprovingWithPaymentKey();
}
