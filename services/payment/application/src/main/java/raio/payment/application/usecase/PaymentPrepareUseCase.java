package raio.payment.application.usecase;

import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.domain.Payment;
public interface PaymentPrepareUseCase {

    Payment prepare(PrepareCommand command);
}
