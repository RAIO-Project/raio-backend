package raio.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.user.domain.type.UserRole;
import raio.user.domain.type.UserStatus;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    
    /** 사용자 ID (PK, Snowflake) */
    private Long id;
    
    /** 이메일 */
    private String email;
    
    /** 비밀번호 */
    private String password;
    
    /** 닉네임 */
    private String nickname;
    
    /** 휴대폰 번호 */
    private String phoneNumber;
    
    /** 권한 (USER / ADMIN) */
    private UserRole role;
    
    /** 상태 (ACTIVE / SUSPENDED / REMOVED) */
    private UserStatus status;
    
    /** 마지막 로그인 일시 */
    private Instant lastLoginAt;
}