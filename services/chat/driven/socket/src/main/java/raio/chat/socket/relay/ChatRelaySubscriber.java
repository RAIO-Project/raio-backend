package raio.chat.socket.relay;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Redis 채널(chat:stream:*) 을 구독해, 수신 메시지를 이 인스턴스의 WebSocket 구독자에게
 * STOMP 토픽으로 전달한다. (인스턴스 간 fan-out 수신 측)
 *
 * <p>chat 채널엔 CHAT/JOIN/LEAVE 가 섞여 흐른다. 구독자는 특정 타입으로 역직렬화하지 않고
 * 페이로드를 그대로(Map) 전달한다 — 프론트가 type 으로 분기. 새 타입이 추가돼도 이 코드는 불변.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRelaySubscriber implements MessageListener {

    private final RedisMessageListenerContainer listenerContainer;
    private final SimpMessagingTemplate messaging;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void registerSubscription() {
        listenerContainer.addMessageListener(this, new PatternTopic(ChatChannel.REDIS_PATTERN));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            String streamId = ChatChannel.streamIdFromChannel(channel);

            // 타입 결합 없이 payload 를 그대로 통과 (type 분기는 클라이언트가)
            Map<String, Object> payload = objectMapper.readValue(message.getBody(), Map.class);

            messaging.convertAndSend(ChatChannel.stompTopic(streamId), payload);
        } catch (Exception e) {
            log.error("Redis 메시지 릴레이 실패", e);
        }
    }
}