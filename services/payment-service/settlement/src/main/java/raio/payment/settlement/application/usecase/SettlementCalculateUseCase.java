package raio.payment.settlement.application.usecase;

import raio.payment.settlement.application.command.SettlementCommands.SettlementCalculateCommand;
import raio.payment.settlement.domain.Settlement;

public interface SettlementCalculateUseCase {

    Settlement calculate(SettlementCalculateCommand command);
}