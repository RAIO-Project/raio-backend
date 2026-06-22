package raio.chat.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.relay.BlindMessage;
import raio.chat.socket.relay.ChatMessage;
import raio.chat.socket.relay.PresenceMessage;
import raio.socket.relay.RelayMessage;
import raio.socket.relay.StreamRelayChannel;

import java.time.Instant;

/**
 * {@link ChatBroadcastPort} 의 Redis publish 구현. 공용 채널(stream:events:{id})로 발행하고
 * core RelaySubscriber 가 /topic/streams/{id} 로 전달한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBroadcastAdapter implements ChatBroadcastPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastMessage(Long streamId, ChatLogs chatLogs, String senderNickname) {
        publish(streamId, new ChatMessage(
                chatLogs.getStreamId(),
                chatLogs.getId(),
                chatLogs.getUserId(),
                senderNickname,
                chatLogs.getMessage(),
                chatLogs.isBlocked(),
                chatLogs.getBlockedReason(),
                Instant.now()));
    }

    @Override
    public void broadcastUserJoined(Long streamId, Long userId, String nickname) {
        publish(streamId, PresenceMessage.join(
                String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    @Override
    public void broadcastUserLeft(Long streamId, Long userId, String nickname) {
        publish(streamId, PresenceMessage.leave(
                String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    @Override
    public void broadcastBlind(Long streamId, String chatId, String reason) {
        publish(streamId, new BlindMessage(String.valueOf(streamId), chatId, reason, Instant.now()));
    }

    private void publish(Long streamId, RelayMessage payload) {
        if (streamId == null) {
            log.warn("streamId 가 null 이라 publish 생략: {}", payload.type());
            return;
        }
        try {
            stringRedisTemplate.convertAndSend(
                    StreamRelayChannel.redisChannel(streamId), objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.error("publish 실패 - type: {}, streamId: {}", payload.type(), streamId, e);
        }
    }
}