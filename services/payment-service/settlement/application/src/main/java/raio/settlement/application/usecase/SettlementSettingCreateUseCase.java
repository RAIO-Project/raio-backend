package raio.settlement.application.usecase;

import raio.settlement.domain.SettlementSetting;

public interface SettlementSettingCreateUseCase {
    
    SettlementSetting createSettlementSetting(String streamerId);
}
