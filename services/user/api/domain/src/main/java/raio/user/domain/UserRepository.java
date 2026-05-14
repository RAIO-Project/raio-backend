package raio.user.domain;

import java.util.Optional;

/** 사용자 저장소 포트 인터페이스. 실제 구현은 driven/rdb 어댑터에 위치. */
public interface UserRepository {
    Users save(Users user);
    Optional<Users> findByEmail(String email);
    Optional<Users> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
