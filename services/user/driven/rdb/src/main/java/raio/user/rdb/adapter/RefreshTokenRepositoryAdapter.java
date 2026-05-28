package raio.user.rdb.adapter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import raio.user.application.properties.AuthProperties;
import raio.user.application.port.RefreshTokenRepository;

import java.util.Optional;

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
}
