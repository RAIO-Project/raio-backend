package raio.user.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raio.jpa.support.SnowflakeBaseTimeEntity;
import raio.user.domain.Users;
import raio.user.domain.type.UserRole;
import raio.user.domain.type.UserStatus;

import java.time.Instant;

@Entity
@Table(name = "users", schema = "`user`")
@SuperBuilder
@NoArgsConstructor
@Getter
public class UserJpaEntity extends SnowflakeBaseTimeEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    private Instant lastLoginAt;

    public Users toDomain() {
        return Users.builder()
                .id(getId())
                .email(email)
                .password(password)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(role)
                .status(status)
                .lastLoginAt(lastLoginAt)
                .build();
    }

    public static UserJpaEntity from(Users user) {
        return UserJpaEntity.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
