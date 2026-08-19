package raio.settlement.application.usecase;

import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;

public interface SettlementSettingUpdateUseCase {
    
    SettlementSettingSummary changeCycle(SettlementCycleChangeCommand command);
    
    SettlementSettingSummary cancelCycleChange(String streamerId);
}