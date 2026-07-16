package raio.stream.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.stream.application.port.StreamViewerSessionCommandPort;
import raio.stream.application.port.StreamViewerSessionQueryPort;

import java.time.Duration;

/**
 * 시청자 세션 매핑의 Redis 구현. 집계된 세션만 스트림에 묶어 둔다.
 * ({@link StreamViewerSessionCommandPort} / {@link StreamViewerSessionQueryPort})
 */
@Component
@RequiredArgsConstructor
public class StreamViewerSessionRedisAdapter
        implements StreamViewerSessionCommandPort, StreamViewerSessionQueryPort {

    private static final String KEY_PREFIX = "stream:viewer:session:";
    /** 서버 비정상 종료 등으로 남는 매핑에 대한 안전장치. 방송 최대 길이보다 넉넉히. */
    private static final Duration TTL = Duration.ofHours(12);

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isBound(String sessionId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key(sessionId)));
    }

    @Override
    public void bind(String sessionId, String streamId) {
        redisTemplate.opsForValue().set(key(sessionId), streamId, TTL);
    }

    @Override
    public String unbind(String sessionId) {
        String streamId = redisTemplate.opsForValue().get(key(sessionId));
        if (streamId != null) {
            redisTemplate.delete(key(sessionId));
        }
        return streamId;
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}