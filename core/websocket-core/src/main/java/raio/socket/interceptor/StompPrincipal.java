package raio.socket.interceptor;

import java.security.Principal;

/**
 * STOMP 세션에 심는 인증 신원. getName()=userId, nickname 도 함께 보관.
 * 세션 attribute 대신 Principal 로 보관해야 CONNECT → 이후 프레임으로 유지된다.
 */
public record StompPrincipal(String userId, String nickname) implements Principal {
    @Override
    public String getName() {
        return userId;
    }
}