package raio.settlement.application.usecase;

import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;

public interface SettlementSettingUseCase {
    
    SettlementSettingSummary getSettlementSetting(String streamerId);
    
    SettlementSettingSummary changeCycle(SettlementCycleChangeCommand command);
    
    SettlementSettingSummary cancelCycleChange(String streamerId);
}