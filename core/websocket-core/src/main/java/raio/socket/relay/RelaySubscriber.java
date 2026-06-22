package raio.socket.relay;

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
 * 공용 relay 수신기. stream:events:* 채널을 구독해, 수신한 메시지를 이 인스턴스에 연결된
 * WebSocket 구독자에게 STOMP 토픽(/topic/streams/{id})으로 전달한다. (인스턴스 간 fan-out 수신측)
 *
 * <p>도메인 타입을 모른다 — payload 를 Map 으로 그대로 통과시키고, type 분기는 클라이언트가 한다.
 * 따라서 chat/donation 등 새 메시지 타입이 추가돼도 이 클래스는 불변. publish 는 각 도메인 어댑터가 담당.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RelaySubscriber implements MessageListener {

    private final RedisMessageListenerContainer listenerContainer;
    private final SimpMessagingTemplate messaging;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void registerSubscription() {
        listenerContainer.addMessageListener(this, new PatternTopic(StreamRelayChannel.REDIS_PATTERN));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            String streamId = StreamRelayChannel.streamIdFromChannel(channel);

            Map<String, Object> payload = objectMapper.readValue(message.getBody(), Map.class);
            messaging.convertAndSend(StreamRelayChannel.stompTopic(streamId), payload);
        } catch (Exception e) {
            log.error("relay 메시지 전달 실패", e);
        }
    }
}