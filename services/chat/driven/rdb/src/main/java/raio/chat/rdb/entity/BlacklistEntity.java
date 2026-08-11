package raio.chat.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raio.jpa.support.SnowflakeBaseCreatedEntity;

import java.time.Instant;


@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "chat", name = "blacklist")
public class BlacklistEntity extends SnowflakeBaseCreatedEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "unblock_at", nullable = false)
    private Instant unblockAt;
}
