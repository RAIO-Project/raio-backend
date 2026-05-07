package raio.chat.driving.websocket.handler;

import lombok.RequiredArgsConstructor;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.domain.ChatLogs;
import raio.chat.driving.websocket.dto.ChatWebSocketDto.ChatBroadcastEvent;
import raio.chat.driving.websocket.dto.ChatWebSocketDto.StreamPresenceEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ChatBroadcastAdapter implements ChatBroadcastPort {

    private static final String TOPIC = "/topic/streams/";
    private final SimpMessagingTemplate messaging;

    @Override
    public void broadcastMessage(Long streamId, ChatLogs chatLogs, String senderNickname) {
        var event = new ChatBroadcastEvent(
                "CHAT",
                chatLogs.getStreamId(),
                chatLogs.getUserId(),
                senderNickname,          // ← 여기서 사용
                chatLogs.getMessage(),
                chatLogs.isBlocked(),
                chatLogs.getBlockedReason(),
                Instant.now()
        );
        messaging.convertAndSend(TOPIC + streamId, event);
    }

    @Override
    public void broadcastUserJoined(Long streamId, Long userId, String nickname) {
        messaging.convertAndSend(TOPIC + streamId,
                new StreamPresenceEvent("JOIN", streamId, userId, nickname, Instant.now()));
    }

    @Override
    public void broadcastUserLeft(Long streamId, Long userId, String nickname) {
        messaging.convertAndSend(TOPIC + streamId,
                new StreamPresenceEvent("LEAVE", streamId, userId, nickname, Instant.now()));
    }
}