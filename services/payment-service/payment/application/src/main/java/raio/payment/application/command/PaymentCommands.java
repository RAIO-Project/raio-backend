package raio.payment.application.command;

import raio.payment.domain.type.PaymentMethod;
import raio.payment.domain.type.PgProvider;

public final class PaymentCommands {
    
    public record PrepareCommand(
            String userId,
            Long amount,
            PaymentMethod method,
            PgProvider pgProvider
    ) {
    }
    
    public record ConfirmCommand(
            String paymentId,
            String externalKey,
            String orderId,
            Long amount
    ) {
    }
}
