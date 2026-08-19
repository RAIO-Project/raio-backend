package raio.user.application.port;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(Long userId, String token);
    Optional<String> findByUserId(Long userId);
    void delete(Long userId);
    long countAll();
}
