package raio.chat.rdb.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ChatLogsEntityId implements Serializable {
    private Long id;
    private Instant createdAt;
}