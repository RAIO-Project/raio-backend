package raio.payment.application.usecase.payment;

import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.domain.payment.Payment;

public interface PaymentConfirmUseCase {

    Payment confirm(ConfirmCommand command);
}
