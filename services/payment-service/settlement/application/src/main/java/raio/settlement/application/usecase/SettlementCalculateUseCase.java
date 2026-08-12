package raio.settlement.application.usecase;

import raio.settlement.application.command.SettlementCommands.SettlementCalculateCommand;
import raio.settlement.domain.Settlement;

public interface SettlementCalculateUseCase {

    Settlement calculate(SettlementCalculateCommand command);
}