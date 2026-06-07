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

/**
 * Redis 채널(chat:stream:*) 을 구독해, 수신 메시지를 이 인스턴스에 연결된
 * WebSocket 구독자에게 STOMP 토픽으로 전달한다. (인스턴스 간 fan-out 수신 측)
 *
 * <p>리스너 컨테이너는 core(redis-api)의 범용 빈을 주입받고, 자신의 구독 채널은
 * {@link PostConstruct} 에서 직접 등록한다. (core 는 chat 을 의존하지 않음 → 의존 방향 chat→core 유지)
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

            ChatRelayMessage payload =
                    objectMapper.readValue(message.getBody(), ChatRelayMessage.class);

            messaging.convertAndSend(ChatChannel.stompTopic(streamId), payload);
        } catch (Exception e) {
            log.error("Redis 메시지 릴레이 실패", e);
        }
    }
}