package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.BlacklistQueryPort;

import java.time.Duration;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class BlacklistQueryAdapter implements BlacklistQueryPort {

    /** 차단 상태가 아님을 캐싱해 두는 기간. 대부분의 채팅이 여기서 걸러져 DB까지 가지 않는다. */
    private static final Duration NOT_BLOCKED_TTL = Duration.ofMinutes(5);

    private final BlacklistJpaRepository blacklistJpaRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean existsActiveByUserId(String userId) {
        String key = BlacklistCache.key(userId);

        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return BlacklistCache.BLOCKED.equals(cached);
        }

        // 캐시 미스면 DB가 기준. Redis가 비어 있어도(재시작·유실) 차단이 그대로 유지된다.
        Instant now = Instant.now();
        Instant unblockAt = blacklistJpaRepository.findLatestUnblockAt(Long.parseLong(userId));

        if (unblockAt == null || !unblockAt.isAfter(now)) {
            redisTemplate.opsForValue().set(key, BlacklistCache.NOT_BLOCKED, NOT_BLOCKED_TTL);
            return false;
        }

        redisTemplate.opsForValue().set(key, BlacklistCache.BLOCKED, Duration.between(now, unblockAt));
        return true;
    }
}
