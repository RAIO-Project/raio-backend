package raio.settlement.adapter.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.settlement.adapter.rdb.entity.SettlementEntity;

import java.time.Instant;
import java.util.Optional;

public interface SettlementJpaRepository extends JpaRepository<SettlementEntity, String> {

    Optional<SettlementEntity> findByStreamerIdAndPeriodStartAtAndPeriodEndAt(
            Long streamerId,
            Instant periodStartAt,
            Instant periodEndAt
    );
}
