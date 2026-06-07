package raio.user.rdb.adapter;

import org.springframework.stereotype.Repository;
import raio.user.application.port.UserRepository;
import raio.user.domain.Users;
import raio.user.exception.UserErrorCode;
import raio.user.rdb.entity.UserJpaEntity;
import raio.user.rdb.repository.UserJpaRepository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Users save(Users user) {
        return userJpaRepository.save(UserJpaEntity.from(user)).toDomain();
    }

    @Override
    public Users update(Users user) {
        UserJpaEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(UserErrorCode.USER_NOT_FOUND::exception);
        entity.updateProfile(user.getNickname(), user.getPhoneNumber());
        entity.updatePassword(user.getPassword());
        return entity.toDomain();
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<Users> findById(Long id) {
        return userJpaRepository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }
}
