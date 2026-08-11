package raio.wallet.adapter.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.wallet.adapter.rdb.entity.PointHistoryEntity;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {
}
