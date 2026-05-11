package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.domain.ChatLogs;
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
        messaging.convertAndSend(TOPIC + streamId,
                new ChatMessagePayload(
                        "CHAT",
                        chatLogs.getStreamId(),
                        chatLogs.getUserId(),
                        senderNickname,
                        chatLogs.getMessage(),
                        chatLogs.isBlocked(),
                        chatLogs.getBlockedReason(),
                        Instant.now()
                ));
    }

    @Override
    public void broadcastUserJoined(Long streamId, Long userId, String nickname) {
        messaging.convertAndSend(TOPIC + streamId,
                new PresencePayload("JOIN", String.valueOf(streamId),
                        String.valueOf(userId), nickname, Instant.now()));
    }

    @Override
    public void broadcastUserLeft(Long streamId, Long userId, String nickname) {
        messaging.convertAndSend(TOPIC + streamId,
                new PresencePayload("LEAVE", String.valueOf(streamId),
                        String.valueOf(userId), nickname, Instant.now()));
    }

    // 이 어댑터 전용 페이로드 - 외부에 노출 안 됨
    private record ChatMessagePayload(
            String type,
            String streamId,
            String userId,
            String senderNickname,
            String message,
            Boolean isBlocked,
            String blockReason,
            Instant createdAt
    ) {}

    private record PresencePayload(
            String type,
            String streamId,
            String userId,
            String nickname,
            Instant occurredAt
    ) {}
}