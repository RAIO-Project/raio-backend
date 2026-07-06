package raio.socket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import raio.jwt.JwtProvider;

/**
 * STOMP CONNECT 프레임에서 JWT 를 검증해 사용자 신원(userId+nickname)을 Principal 로 심는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER)) {
            return message; // 토큰 없음 → 익명
        }

        String token = authorization.substring(BEARER.length());
        if (!jwtProvider.validate(token)) {
            return message; // 검증 실패 → 익명
        }

        // userId + nickname 을 Principal 로 심는다 (세션 전체 유지)
        String userId = jwtProvider.extractUserId(token);
        String nickname = jwtProvider.extractNickName(token);
        accessor.setUser(new StompPrincipal(userId, nickname));
        return message;
    }
}