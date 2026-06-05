package raio.chat.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raio.jpa.support.SnowflakeBaseCreatedEntity;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "chat", name = "chat_logs")
public class ChatLogsEntity extends SnowflakeBaseCreatedEntity {

    @Column(name = "stream_id", nullable = false)
    private Long streamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sender_nickname", length = 50)
    private String senderNickname;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "block_reason", length = 100)
    private String blockReason;
}