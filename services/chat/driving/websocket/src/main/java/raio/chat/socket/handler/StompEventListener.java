package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.socket.interceptor.StompAuthInterceptor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private static final String TOPIC_PREFIX = "/topic/streams/";

    private final ChatBroadcastPort chatBroadcastPort;

    // CONNECT 시 저장
    private final Map<String, Map<String, Object>> sessionAttrs = new ConcurrentHashMap<>();
    // sessionId → (subscriptionId → streamId)
    // DISCONNECT 시 어떤 방에서 나가야 하는지 추적
    private final Map<String, Map<String, Long>> sessions = new ConcurrentHashMap<>();

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var sessionId = accessor.getSessionId();
        var attrs = accessor.getSessionAttributes();
        if (sessionId != null && attrs != null) {
            sessionAttrs.put(sessionId, attrs);  // ← 여기서 저장
        }
    }


    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var dest = accessor.getDestination();
        if (dest == null || !dest.startsWith(TOPIC_PREFIX)) return;

        var streamId = parseStreamId(dest);
        if (streamId == null) return;

        var sessionId = accessor.getSessionId();
        var subscriptionId = accessor.getSubscriptionId();
        var attrs = sessionAttrs.get(sessionId);  // ← 저장해둔 거 꺼냄
        var userId = extractUserId(attrs);
        var nickname = extractNickname(attrs);

        if (sessionId != null && subscriptionId != null) {
            sessions.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
                    .put(subscriptionId, streamId);
        }

        log.debug("입장 - streamId: {}, userId: {}, nickname: {}", streamId, userId, nickname);
        chatBroadcastPort.broadcastUserJoined(streamId, userId, nickname);
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var sessionId = accessor.getSessionId();
        var subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) return;

        var subs = sessions.get(sessionId);
        if (subs == null) return;
        var streamId = subs.remove(subscriptionId);
        if (streamId == null) return;

        var attrs = sessionAttrs.get(sessionId);  // ← 저장해둔 거 꺼냄
        log.debug("퇴장 - streamId: {}, userId: {}", streamId, extractUserId(attrs));
        chatBroadcastPort.broadcastUserLeft(streamId, extractUserId(attrs), extractNickname(attrs));
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        var sessionId = event.getSessionId();
        sessionAttrs.remove(sessionId);  // ← 세션 정리

        var subs = sessions.remove(sessionId);
        if (subs == null) return;
        subs.values().forEach(streamId ->
                chatBroadcastPort.broadcastUserLeft(streamId, -1L, "anonymous")
        );
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
        if (attrs == null || attrs.get(StompAuthInterceptor.USER_ID) == null) return -1L;
        return (Long) attrs.get(StompAuthInterceptor.USER_ID);
    }

    private String extractNickname(Map<String, Object> attrs) {
        if (attrs == null || attrs.get(StompAuthInterceptor.NICKNAME) == null) return "anonymous";
        return (String) attrs.get(StompAuthInterceptor.NICKNAME);
    }
}