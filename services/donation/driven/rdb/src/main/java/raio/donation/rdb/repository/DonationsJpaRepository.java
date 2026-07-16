package raio.donation.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.donation.rdb.entity.DonationsEntity;

public interface DonationsJpaRepository extends JpaRepository<DonationsEntity, Long> {
}
