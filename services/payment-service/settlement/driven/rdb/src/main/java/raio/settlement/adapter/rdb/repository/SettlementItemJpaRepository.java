package raio.settlement.adapter.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.settlement.adapter.rdb.entity.SettlementItemEntity;

public interface SettlementItemJpaRepository extends JpaRepository<SettlementItemEntity, String> {
}
