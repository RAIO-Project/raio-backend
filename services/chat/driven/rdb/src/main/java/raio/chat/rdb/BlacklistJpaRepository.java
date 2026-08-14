package raio.chat.rdb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raio.chat.rdb.entity.BlacklistEntity;

import java.time.Instant;

public interface BlacklistJpaRepository extends JpaRepository<BlacklistEntity, Long> {

    /** 해당 유저의 가장 늦은 차단 해제 시각. 차단 이력이 없으면 null. */
    @Query("SELECT MAX(b.unblockAt) FROM BlacklistEntity b WHERE b.userId = :userId")
    Instant findLatestUnblockAt(@Param("userId") Long userId);
}
