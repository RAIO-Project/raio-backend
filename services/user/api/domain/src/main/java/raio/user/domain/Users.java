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
}
