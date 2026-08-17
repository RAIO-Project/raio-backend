package raio.settlement.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.time.Instant;

public interface SettlementSettingReadUseCase {
    
    SettlementSettingSummary getSettlementSetting(String streamerId);
    
    Page<SettlementSettingSummary> getSettlementDueSettings(Instant now, Pageable pageable);
}
