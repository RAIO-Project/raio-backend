package raio.user.application.port;

import raio.user.domain.Users;

import java.util.Optional;

public interface UserRepository {
    Users save(Users user);
    Users update(Users user);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
