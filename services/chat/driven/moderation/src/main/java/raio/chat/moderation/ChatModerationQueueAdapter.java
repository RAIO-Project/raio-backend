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