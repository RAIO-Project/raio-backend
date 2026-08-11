package raio.chat.rdb;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.chat.rdb.entity.BlacklistEntity;

import java.time.Instant;

public interface BlacklistJpaRepository extends JpaRepository<BlacklistEntity, Long> {
    boolean existsByUserIdAndUnblockAtAfter(Long userId, Instant now);
}
