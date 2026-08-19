package raio.wallet.application.port;

import raio.wallet.domain.PointHistory;
import raio.wallet.domain.type.PointHistoryType;

import java.util.Optional;

public interface PointHistoryCommandRepositoryPort {

    PointHistory save(PointHistory history);

    /**
     * 멱등성 검증을 위해 동일 지갑/유형/원인 이벤트로 이미 기록된 이력이 있는지 조회한다.
     */
    Optional<PointHistory> findByWalletIdAndTypeAndSourceId(String walletId, PointHistoryType type, String sourceId);
}
