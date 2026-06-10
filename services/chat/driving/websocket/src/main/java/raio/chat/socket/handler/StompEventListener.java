package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import raio.chat.application.port.ChatBroadcastPort;
import raio.socket.interceptor.StompAuthChannelInterceptor;

import java.util.Map;

/**
 * STOMP 구독 → 입장 알림.
 *
 * <p>치지직/숲 방식: 비로그인(익명) 입장은 띄우지 않고(로그인 유저만 JOIN), 개별 퇴장(LEAVE)은 띄우지 않는다.
 * 세션 속성(userId)은 구독 이벤트의 accessor 에서 직접 읽는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private static final String TOPIC_PREFIX = "/topic/streams/";
    private static final long ANONYMOUS_USER_ID = -1L;

    private final ChatBroadcastPort chatBroadcastPort;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var dest = accessor.getDestination();
        if (dest == null || !dest.startsWith(TOPIC_PREFIX)) return;

        var streamId = parseStreamId(dest);
        if (streamId == null) return;

        var attrs = accessor.getSessionAttributes();
        var userId = extractUserId(attrs);

        // 비로그인(익명) 입장은 JOIN 생략 (읽기는 허용)
        if (!isLoggedIn(userId)) {
            log.debug("익명 입장 - JOIN 생략 (streamId: {})", streamId);
            return;
        }

        var nickname = extractNickname(attrs, userId);
        log.debug("입장 - streamId: {}, userId: {}, nickname: {}", streamId, userId, nickname);
        chatBroadcastPort.broadcastUserJoined(streamId, userId, nickname);
    }

    private boolean isLoggedIn(Long userId) {
        return userId != null && userId != ANONYMOUS_USER_ID;
    }

    private Long parseStreamId(String dest) {
        try {
            return Long.parseLong(dest.substring(TOPIC_PREFIX.length()));
        } catch (NumberFormatException e) {
            log.warn("streamId 파싱 실패: {}", dest);
            return null;
        }
    }

    private Long extractUserId(Map<String, Object> attrs) {
        if (attrs == null || attrs.get(StompAuthChannelInterceptor.USER_ID) == null) return ANONYMOUS_USER_ID;
        return (Long) attrs.get(StompAuthChannelInterceptor.USER_ID);
    }

    // 토큰에 nickname claim 이 없어 현재는 userId 를 표시명으로 사용
    // TODO(nickname): 토큰 claim 추가되면 세션의 NICKNAME 을 사용
    private String extractNickname(Map<String, Object> attrs, Long userId) {
        if (attrs != null && attrs.get(StompAuthChannelInterceptor.NICKNAME) != null) {
            return (String) attrs.get(StompAuthChannelInterceptor.NICKNAME);
        }
        return String.valueOf(userId);
    }
}