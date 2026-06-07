package raio.payment.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.payment.rdb.entity.PointHistoryEntity;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistoryEntity, Long> {
}
