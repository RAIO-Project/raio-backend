package raio.user.rdb.adapter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import raio.user.domain.RefreshTokenRepository;

import java.time.Duration;
import java.util.Optional;

/** RefreshTokenRepository 포트의 Redis 구현체. TTL을 Redis에 위임해 자동 만료 처리 */
@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh_token:";

    private final StringRedisTemplate redisTemplate;

    public RefreshTokenRepositoryAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Long userId, String token, Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, token, ttl);
    }

    @Override
    public Optional<String> findByUserId(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + userId));
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
