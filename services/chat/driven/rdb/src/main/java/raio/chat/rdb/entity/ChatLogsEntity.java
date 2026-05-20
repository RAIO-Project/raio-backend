package raio.chat.rdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
@IdClass(ChatLogsEntityId.class)
@Table(schema = "chat", name = "chat_logs")
@EntityListeners(AuditingEntityListener.class)
public class ChatLogsEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "stream_id", nullable = false)
    private Long streamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sender_nickname", length = 50)  // ← nullable 제거
    private String senderNickname;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "block_reason", length = 100)
    private String blockReason;

    @Id
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}