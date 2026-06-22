package raio.chat.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.relay.ChatMessage;
import raio.chat.socket.relay.PresenceMessage;
import raio.socket.relay.RelayMessage;
import raio.socket.relay.StreamRelayChannel;

import java.time.Instant;

/**
 * {@link ChatBroadcastPort} 의 Redis publish 구현.
 *
 * <p>chat 도메인 메시지(ChatMessage/PresenceMessage)를 공용 채널(stream:events:{id})로 발행.
 * 수신/전달은 core 의 공용 RelaySubscriber 가 처리한다(인스턴스 간 fan-out).
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
        publish(streamId, PresenceMessage.join(
                String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    @Override
    public void broadcastUserLeft(Long streamId, Long userId, String nickname) {
        publish(streamId, PresenceMessage.leave(
                String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    private void publish(Long streamId, RelayMessage payload) {
        if (streamId == null) {
            log.warn("streamId 가 null 이라 publish 생략: {}", payload.type());
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(payload);
            stringRedisTemplate.convertAndSend(StreamRelayChannel.redisChannel(streamId), json);
        } catch (Exception e) {
            log.error("채팅 메시지 publish 실패 - streamId: {}", streamId, e);
        }
    }
}