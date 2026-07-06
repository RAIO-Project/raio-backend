package raio.chat.rdb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raio.chat.rdb.entity.ChatLogsEntity;

public interface ChatLogsJpaRepository extends JpaRepository<ChatLogsEntity, Long> {
    @Modifying
    @Query("UPDATE ChatLogsEntity c SET c.isBlocked = true, c.blockReason = :reason WHERE c.id = :id")
    void markBlocked(@Param("id") Long id, @Param("reason") String reason);
}