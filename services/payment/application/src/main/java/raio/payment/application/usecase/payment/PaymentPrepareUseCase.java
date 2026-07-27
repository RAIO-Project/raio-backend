package raio.payment.application.usecase.payment;

import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.domain.payment.Payment;
public interface PaymentPrepareUseCase {

    Payment prepare(PrepareCommand command);
}
