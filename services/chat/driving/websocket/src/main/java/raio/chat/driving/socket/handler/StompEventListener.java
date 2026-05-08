package raio.chat.driving.socket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.driving.socket.interceptor.StompAuthInterceptor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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

    // sessionId → (subscriptionId → streamId)
    // DISCONNECT 시 어떤 방에서 나가야 하는지 추적
    private final Map<String, Map<String, Long>> sessions = new ConcurrentHashMap<>();

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        var dest = accessor.getDestination();

        // /topic/streams/{streamId} 구독만 처리
        if (dest == null || !dest.startsWith(TOPIC_PREFIX)) return;

        var streamId = parseStreamId(dest);
        if (streamId == null) return;

        var sessionId = accessor.getSessionId();
        var subscriptionId = accessor.getSubscriptionId();
        var attrs = accessor.getSessionAttributes();
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
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        var sessionId = accessor.getSessionId();
        var subscriptionId = accessor.getSubscriptionId();
        if (sessionId == null || subscriptionId == null) return;

        var subs = sessions.get(sessionId);
        if (subs == null) return;

        var streamId = subs.remove(subscriptionId);
        if (streamId == null) return;

        var attrs = accessor.getSessionAttributes();
        log.debug("퇴장 - streamId: {}, userId: {}", streamId, extractUserId(attrs));
        chatBroadcastPort.broadcastUserLeft(streamId, extractUserId(attrs), extractNickname(attrs));
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        var subs = sessions.remove(event.getSessionId());
        if (subs == null) return;

        // 연결 끊기면 들어가있던 모든 방에서 퇴장 처리
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