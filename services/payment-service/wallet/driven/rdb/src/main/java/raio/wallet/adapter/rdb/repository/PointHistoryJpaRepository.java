package raio.wallet.adapter.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.wallet.adapter.rdb.entity.PointHistoryEntity;
import raio.wallet.adapter.rdb.entity.type.PointHistoryEntityType;

import java.util.Optional;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {

    Optional<PointHistoryEntity> findByWalletIdAndTypeAndSourceId(Long walletId, PointHistoryEntityType type, Long sourceId);
}
