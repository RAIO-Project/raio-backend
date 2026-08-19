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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP CONNECT 프레임에서 JWT 를 검증해 사용자 신원(userId+nickname)을 Principal 로 심는다.
 * 세션별 Principal 을 직접 관리하여 MESSAGE 프레임에서도 Principal 이 유지되도록 한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;

    // sessionId → Principal 직접 관리 (Spring 내부 stompAuthentications 의존 X)
    private final Map<String, StompPrincipal> sessionPrincipals = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            log.debug("[STOMP-AUTH] accessor=null, msgType={}", message.getHeaders().get("simpMessageType"));
            return message;
        }

        StompCommand command = accessor.getCommand();
        log.debug("[STOMP-AUTH] command={} sessionId={}", command, accessor.getSessionId());

        if (StompCommand.CONNECT.equals(command)) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            if (authorization != null && authorization.startsWith(BEARER)) {
                String token = authorization.substring(BEARER.length());
                if (jwtProvider.validate(token)) {
                    String userId = jwtProvider.extractUserId(token);
                    String nickname = jwtProvider.extractNickName(token);
                    StompPrincipal principal = new StompPrincipal(userId, nickname);
                    accessor.setUser(principal);
                    sessionPrincipals.put(accessor.getSessionId(), principal);
                    log.debug("[STOMP] CONNECT 인증 성공 sessionId={} userId={}", accessor.getSessionId(), userId);
                } else {
                    log.warn("[STOMP] CONNECT JWT 검증 실패 sessionId={}", accessor.getSessionId());
                }
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            sessionPrincipals.remove(accessor.getSessionId());
        } else {
            // MESSAGE, SUBSCRIBE 등 — 저장된 Principal 복원
            StompPrincipal principal = sessionPrincipals.get(accessor.getSessionId());
            if (principal != null) {
                accessor.setUser(principal);
            }
        }

        return message;
    }
}