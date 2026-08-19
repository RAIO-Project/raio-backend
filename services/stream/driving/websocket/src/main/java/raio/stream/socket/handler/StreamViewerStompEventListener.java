package raio.stream.socket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import raio.socket.interceptor.StompPrincipal;
import raio.socket.relay.StreamRelayChannel;
import raio.stream.application.port.StreamBroadcastPort;
import raio.stream.application.port.StreamViewerSessionCommandPort;
import raio.stream.application.port.StreamViewerSessionQueryPort;
import raio.stream.application.usecase.StreamViewerEnterUseCase;
import raio.stream.application.usecase.StreamViewerExitUseCase;
import raio.stream.application.usecase.StreamViewerReadUseCase;

/**
 * 방 구독/연결 해제 → 실시간 시청자 수 집계.
 * <p>비로그인(익명) 연결도 시청자로 센다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamViewerStompEventListener {

    private final StreamViewerEnterUseCase streamViewerEnterUseCase;
    private final StreamViewerExitUseCase streamViewerExitUseCase;
    private final StreamViewerReadUseCase streamViewerReadUseCase;
    private final StreamViewerSessionCommandPort streamViewerSessionCommandPort;
    private final StreamViewerSessionQueryPort streamViewerSessionQueryPort;
    private final StreamBroadcastPort streamBroadcastPort;

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        var accessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        var destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(StreamRelayChannel.STOMP_TOPIC_PREFIX)) {
            return;
        }

        String streamId = parseStreamId(destination);
        if (streamId == null) return;

        String sessionId = accessor.getSessionId();

        // 중복 구독이면 다시 세지 않고 현재 수만 전달한다.
        if (streamViewerSessionQueryPort.isBound(sessionId)) {
            streamBroadcastPort.broadcastViewerCount(
                    streamId, streamViewerReadUseCase.currentCount(streamId));
            return;
        }

        String viewerId = accessor.getUser() instanceof StompPrincipal user ? user.getName() : null;

        var entered = streamViewerEnterUseCase.enter(streamId, viewerId);
        if (entered.counted()) {
            streamViewerSessionCommandPort.bind(sessionId, streamId);
        }
        streamBroadcastPort.broadcastViewerCount(streamId, entered.viewerCount());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String streamId = streamViewerSessionCommandPort.unbind(event.getSessionId());
        if (streamId == null) return; // 집계 대상이 아니었던 세션

        long viewerCount = streamViewerExitUseCase.exit(streamId);
        streamBroadcastPort.broadcastViewerCount(streamId, viewerCount);
    }

    private String parseStreamId(String destination) {
        String raw = destination.substring(StreamRelayChannel.STOMP_TOPIC_PREFIX.length());
        try {
            Long.parseLong(raw); // 형식 검증
            return raw;
        } catch (NumberFormatException e) {
            log.warn("streamId 파싱 실패: {}", destination);
            return null;
        }
    }
}