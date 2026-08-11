package raio.payment.settlement.application.port;

import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementSettingReadModel;

import java.util.Optional;

public interface SettlementSettingQueryRepositoryPort {

    Optional<SettlementSettingReadModel> findSettlementSettingByStreamerId(String streamerId);
}