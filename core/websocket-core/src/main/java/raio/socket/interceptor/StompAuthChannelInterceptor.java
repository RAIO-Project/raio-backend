package raio.socket.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import raio.jwt.JwtProvider;

import java.util.Map;

/**
 * STOMP CONNECT 프레임에서 JWT 를 검증해 사용자 신원을 세션에 심는다.
 *
 * <p>토큰이 있으면 검증 후 userId 를 세션 속성에 저장(회원), 없거나 검증 실패면 그냥 통과(익명).
 * 익명도 연결·구독(채팅 읽기)은 허용한다. 발송(쓰기) 차단은 ChatStompApi 에서 userId 유무로 강제.
 *
 * <p>기존 쿼리파라미터 방식(StompAuthInterceptor, HandshakeInterceptor)은 userId 위조가 가능해 폐기.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    public static final String USER_ID = "userId";
    public static final String NICKNAME = "nickname";
    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // CONNECT 일 때만 인증 처리 (이후 프레임은 세션 속성 재사용)
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader("Authorization");
        // 토큰 없음 → 익명 통과 (비회원 읽기 허용)
        if (authorization == null || !authorization.startsWith(BEARER)) {
            return message;
        }

        String token = authorization.substring(BEARER.length());
        // 검증 실패 → 익명 통과 (거부하지 않음)
        if (!jwtProvider.validate(token)) {
            return message;
        }

        Map<String, Object> attrs = accessor.getSessionAttributes();
        if (attrs != null) {
            attrs.put(USER_ID, Long.parseLong(jwtProvider.extractUserId(token)));
             attrs.put(NICKNAME, jwtProvider.extractNickName(token));
        }
        return message;
    }
}