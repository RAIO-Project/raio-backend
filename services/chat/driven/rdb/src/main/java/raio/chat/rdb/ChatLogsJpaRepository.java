package raio.chat.rdb;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.chat.rdb.entity.ChatLogsEntity;

public interface ChatLogsJpaRepository extends JpaRepository<ChatLogsEntity, Long> {
}