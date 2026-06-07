package raio.chat.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.relay.ChatChannel;
import raio.chat.socket.relay.ChatRelayMessage;

import java.time.Instant;

/**
 * {@link ChatBroadcastPort} 의 Redis pub/sub publish 구현.
 *
 * <p>기존엔 SimpMessagingTemplate 로 자기 인스턴스 브로커에만 전달했으나(다중 인스턴스에서 분리됨),
 * 이제 Redis 채널로 publish 한다. 실제 WebSocket 구독자 전달은 각 인스턴스의
 * {@link raio.chat.socket.relay.ChatRelaySubscriber} 가 수신해서 처리한다. (인스턴스 간 fan-out)
 *
 * <p>직렬화는 ObjectMapper 평문 JSON 으로 통일한다(subscriber 와 동일). StringRedisTemplate 사용.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBroadcastAdapter implements ChatBroadcastPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastMessage(Long streamId, ChatLogs chatLogs, String senderNickname) {
        publish(streamId, ChatRelayMessage.chat(
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
        publish(streamId, ChatRelayMessage.presence(
                "JOIN", String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    @Override
    public void broadcastUserLeft(Long streamId, Long userId, String nickname) {
        publish(streamId, ChatRelayMessage.presence(
                "LEAVE", String.valueOf(streamId), String.valueOf(userId), nickname, Instant.now()));
    }

    private void publish(Long streamId, ChatRelayMessage payload) {
        if (streamId == null) {
            log.warn("streamId 가 null 이라 publish 생략: {}", payload.type());
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(payload);
            stringRedisTemplate.convertAndSend(ChatChannel.redisChannel(streamId), json);
        } catch (Exception e) {
            log.error("채팅 메시지 publish 실패 - streamId: {}", streamId, e);
        }
    }
}