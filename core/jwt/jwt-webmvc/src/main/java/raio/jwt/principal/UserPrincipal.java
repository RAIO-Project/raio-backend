package raio.jwt.principal;

import java.security.Principal;

/**
 * REST 인증 신원. JwtAuthenticationFilter 가 SecurityContext 의 principal 로 심는다.
 *
 * <p>userId 외에 표시용 nickname 까지 보관해, 컨트롤러가 요청 본문 대신 토큰의 신원을 쓰게 한다.
 * (STOMP 측 StompPrincipal 의 REST 짝. getName()=userId 로 기존 authentication.getName() 동작을 유지한다.)
 */
public record UserPrincipal(String userId, String nickname) implements Principal {
    @Override
    public String getName() {
        return userId;
    }
}
