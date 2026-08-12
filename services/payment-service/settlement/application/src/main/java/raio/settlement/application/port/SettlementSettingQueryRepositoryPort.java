package raio.settlement.application.port;

import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.util.Optional;

public interface SettlementSettingQueryRepositoryPort {

    Optional<SettlementSettingSummary> findSettlementSettingByStreamerId(String streamerId);
}