package raio.user.rdb.adapter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import raio.user.application.port.UserRepository;
import raio.user.domain.Users;
import raio.user.exception.UserErrorCode;
import raio.user.rdb.entity.UserJpaEntity;
import raio.user.rdb.repository.UserJpaRepository;

import java.util.Optional;

/** UserRepository 포트의 JPA 구현체 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private static final String UK_EMAIL = "uk_users_email";
    private static final String UK_NICKNAME = "uk_users_nickname";

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    /**
     * 유니크 제약 위반을 도메인 예외로 번역한다.
     *
     * <p>RegisterService 는 {@code existsByEmail}/{@code existsByNickname} 으로 중복을 확인한 뒤
     * 저장한다. 그 사이에 BCrypt 해싱(약 70ms)이 들어가므로 동시 요청이 둘 다 검사를 통과할 수 있다.
     * 실제로 같은 닉네임으로 15건을 동시에 보내면 DB 제약이 발동하는 것을 확인했다.
     * 데이터는 제약이 지켜주지만, 번역하지 않으면 사용자는 409 대신 500을 받는다.
     *
     * <p>번역을 application 이 아니라 이 어댑터에서 하는 이유 — {@code DataIntegrityViolationException}
     * 은 영속화 기술의 어휘다. application 모듈은 spring-tx 에 의존하지 않으므로 그쪽에서 잡을 수도 없다.
     *
     * <p>{@code saveAndFlush} 를 쓰는 이유 — {@code save} 는 INSERT 를 트랜잭션 커밋까지 미룰 수 있어
     * 이 메서드가 반환된 뒤에 예외가 터진다. 그러면 아래 catch 를 그냥 지나친다.
     * 즉시 flush 해서 위반을 여기서 받는다.
     */
    @Override
    public Users save(Users user) {
        try {
            return userJpaRepository.saveAndFlush(UserJpaEntity.from(user)).toDomain();
        } catch (DataIntegrityViolationException e) {
            throw translate(e);
        }
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

    /** 아는 제약이면 도메인 예외로, 모르는 제약이면 원래 예외를 그대로 던진다(문제를 숨기지 않는다). */
    private RuntimeException translate(DataIntegrityViolationException e) {
        String constraint = constraintNameOf(e);
        if (constraint == null) {
            return e;
        }
        if (constraint.contains(UK_EMAIL)) {
            return UserErrorCode.EMAIL_ALREADY_EXISTS.exception(e);
        }
        if (constraint.contains(UK_NICKNAME)) {
            return UserErrorCode.NICKNAME_ALREADY_EXISTS.exception(e);
        }
        return e;
    }

    /**
     * 위반된 제약 이름을 찾는다.
     * Hibernate 의 ConstraintViolationException 이 이름을 들고 있지만, 드라이버·버전에 따라
     * 비어 있을 수 있어 메시지까지 훑는다. 둘 다 없으면 null.
     */
    private String constraintNameOf(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof org.hibernate.exception.ConstraintViolationException hce) {
                String name = hce.getConstraintName();
                if (name != null && !name.isBlank()) {
                    return name.toLowerCase();
                }
            }
            String message = t.getMessage();
            if (message != null) {
                String lower = message.toLowerCase();
                if (lower.contains(UK_EMAIL) || lower.contains(UK_NICKNAME)) {
                    return lower;
                }
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return null;
    }
}
