package raio.payment.settlement.application.usecase;

import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementSettingReadModel;
import raio.payment.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;

public interface SettlementSettingUseCase {

    SettlementSettingReadModel getSettlementSetting(String streamerId);

    SettlementSettingReadModel changeCycle(SettlementCycleChangeCommand command);

    SettlementSettingReadModel cancelCycleChange(String streamerId);
}