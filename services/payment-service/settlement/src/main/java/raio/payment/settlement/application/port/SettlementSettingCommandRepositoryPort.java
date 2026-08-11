package raio.payment.settlement.application.port;

import raio.payment.settlement.domain.SettlementSetting;

import java.util.Optional;

public interface SettlementSettingCommandRepositoryPort {

    Optional<SettlementSetting> findByStreamerId(String streamerId);

    SettlementSetting save(SettlementSetting setting);
}