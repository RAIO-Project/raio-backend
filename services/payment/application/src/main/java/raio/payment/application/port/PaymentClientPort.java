package raio.payment.application.port;

import raio.payment.PaymentReadModels.PaymentConfirmResult;

public interface PaymentClientPort {
    
    PaymentConfirmResult confirm(String externalKey, String orderId, Long amount);
}
