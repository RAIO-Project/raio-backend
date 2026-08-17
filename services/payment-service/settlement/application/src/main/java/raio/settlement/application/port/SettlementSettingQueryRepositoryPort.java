package raio.settlement.application.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.time.Instant;
import java.util.Optional;

public interface SettlementSettingQueryRepositoryPort {

    Optional<SettlementSettingSummary> findSettlementSettingByStreamerId(String streamerId);
    
    Page<SettlementSettingSummary> findSettlementDueSettings(boolean active, Instant settlementStartAt, Pageable pageable);
}