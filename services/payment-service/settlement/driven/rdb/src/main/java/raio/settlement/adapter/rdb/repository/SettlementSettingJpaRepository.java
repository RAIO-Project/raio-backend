package raio.settlement.adapter.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.settlement.adapter.rdb.entity.SettlementSettingEntity;

public interface SettlementSettingJpaRepository extends JpaRepository<SettlementSettingEntity, Long> {
}
