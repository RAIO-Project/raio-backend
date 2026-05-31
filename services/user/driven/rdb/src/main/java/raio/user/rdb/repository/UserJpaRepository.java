package raio.user.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.user.rdb.entity.UserJpaEntity;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
