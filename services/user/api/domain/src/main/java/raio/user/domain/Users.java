package raio.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import raio.user.domain.type.UserRole;
import raio.user.domain.type.UserStatus;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
    private Instant lastLoginAt;

    public Users withProfile(String nickname, String phoneNumber) {
        return Users.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .nickname(nickname != null ? nickname : this.nickname)
                .phoneNumber(phoneNumber != null ? phoneNumber : this.phoneNumber)
                .role(this.role)
                .status(this.status)
                .lastLoginAt(this.lastLoginAt)
                .build();
    }

    public Users withPassword(String encodedPassword) {
        return Users.builder()
                .id(this.id)
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .role(this.role)
                .status(this.status)
                .lastLoginAt(this.lastLoginAt)
                .build();
    }
}
