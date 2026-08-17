package raio.wallet.application.port;

import raio.settlement.domain.SettlementSetting;

public interface SettlementCommandPort {

    SettlementSetting save(String streamerId);
}
