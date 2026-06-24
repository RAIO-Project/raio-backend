package raio.user.rdb.adapter;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import raio.user.application.properties.AuthProperties;
import raio.user.application.port.RefreshTokenRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** RefreshTokenRepository 포트의 Redis 구현체. TTL은 AuthProperties에서 읽어 자동 만료 처리 */
@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh_token:";

    private final StringRedisTemplate redisTemplate;
    private final AuthProperties authProperties;

    public RefreshTokenRepositoryAdapter(StringRedisTemplate redisTemplate, AuthProperties authProperties) {
        this.redisTemplate = redisTemplate;
        this.authProperties = authProperties;
    }

    @Override
    public void save(Long userId, String token) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, token, authProperties.refreshTokenTtl());
    }

    @Override
    public Optional<String> findByUserId(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + userId));
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

    @Override
    public long countAll() {
        AtomicLong count = new AtomicLong(0);
        ScanOptions options = ScanOptions.scanOptions()
                .match(KEY_PREFIX + "*")
                .count(100)
                .build();
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                cursor.forEachRemaining(key -> count.incrementAndGet());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        return count.get();
    }
}
