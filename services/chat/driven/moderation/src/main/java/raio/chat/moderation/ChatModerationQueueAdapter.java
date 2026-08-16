package raio.chat.moderation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ChatModerationPort;

import java.util.Map;

/** {@link ChatModerationPort} 구현 — 채팅을 Redis Streams 에 적재. */
@Component
@RequiredArgsConstructor
public class ChatModerationQueueAdapter implements ChatModerationPort {

    public static final String STREAM_KEY = "moderation:chat";

    /** 컨슈머 그룹. 소비·ack·회수가 모두 이 이름을 공유하므로 한곳에서 관리한다. */
    public static final String CONSUMER_GROUP = "moderation-workers";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void enqueue(String chatId, String streamId, String message) {
        var record = StreamRecords.mapBacked(Map.of(
                "chatId", chatId,
                "streamId", streamId,
                "message", message
        )).withStreamKey(STREAM_KEY);
        stringRedisTemplate.opsForStream().add(record);
    }
}
