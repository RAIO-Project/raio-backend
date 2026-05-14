package raio.user.domain;

import java.time.Duration;
import java.util.Optional;

/** RefreshToken 저장소 포트 인터페이스. 실제 구현은 Redis 어댑터에 위치. */
public interface RefreshTokenRepository {
    /** userId 키로 토큰을 저장. TTL 설정으로 자동 만료 */
    void save(Long userId, String token, Duration ttl);
    Optional<String> findByUserId(Long userId);
    void delete(Long userId);
}
