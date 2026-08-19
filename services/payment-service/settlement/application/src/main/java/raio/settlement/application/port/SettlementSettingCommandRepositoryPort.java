package raio.settlement.application.port;

import raio.settlement.domain.SettlementSetting;

import java.util.Optional;
import java.util.function.Supplier;

public interface SettlementSettingCommandRepositoryPort {

    Optional<SettlementSetting> findByStreamerId(String streamerId);

    SettlementSetting save(SettlementSetting setting);

    /**
     * 호출부의 트랜잭션 결과와 무관하게 항상 커밋되어야 하는 작업을 별도 트랜잭션으로 실행한다.
     */
    <T> T transactionRequiresNew(Supplier<T> supplier);
}