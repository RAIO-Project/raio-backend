package raio.payment.application.usecase;

import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.domain.Payment;

public interface PaymentConfirmUseCase {

    Payment confirm(ConfirmCommand command);
}
