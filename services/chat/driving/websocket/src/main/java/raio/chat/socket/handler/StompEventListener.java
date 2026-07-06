package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import raio.chat.application.port.ChatBroadcastPort;
import raio.socket.interceptor.StompPrincipal;

/**
 * STOMP 구독 → 입장(JOIN) 알림. 신원은 Principal에서 읽는다.
 * 로그인 유저만 JOIN, 익명 입장·개별 퇴장은 안 띄움.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private static final String TOPIC_PREFIX = "/topic/streams/";

    private final ChatBroadcastPort chatBroadcastPort;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var dest = accessor.getDestination();
        if (dest == null || !dest.startsWith(TOPIC_PREFIX)) return;

        var streamId = parseStreamId(dest);
        if (streamId == null) return;

        // 비로그인이면 JOIN 생략
        if (!(accessor.getUser() instanceof StompPrincipal user)) {
            log.debug("익명 입장 - JOIN 생략 (streamId: {})", streamId);
            return;
        }

        Long userId = Long.parseLong(user.getName());
        String nickname = user.nickname() != null ? user.nickname() : String.valueOf(userId);
        chatBroadcastPort.broadcastUserJoined(streamId, userId, nickname);
    }

    private Long parseStreamId(String dest) {
        try {
            return Long.parseLong(dest.substring(TOPIC_PREFIX.length()));
        } catch (NumberFormatException e) {
            log.warn("streamId 파싱 실패: {}", dest);
            return null;
        }
    }
}