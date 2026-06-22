package raio.chat.moderation;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * 모더레이션 큐 컨슈머 등록기. core 의 범용 StreamMessageListenerContainer 에
 * 모더레이션 컨슈머를 등록한다. (RelaySubscriber 가 pub/sub 컨테이너에 등록하는 것과 동일 패턴)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationStreamSubscriber {
    private static final String GROUP = "moderation-workers";
    private static final String CONSUMER = "moderation-worker-1";

    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    private final StringRedisTemplate stringRedisTemplate;
    private final ModerationStreamConsumer consumer;

    @PostConstruct
    void registerSubscription() {
        ensureGroup();
        container.receiveAutoAck(
                Consumer.from(GROUP, CONSUMER),    // @Value consumerName → 상수 CONSUMER
                StreamOffset.create(ChatModerationQueueAdapter.STREAM_KEY, ReadOffset.lastConsumed()),
                consumer);
    }

    /** 컨슈머 그룹이 없으면 생성 (스트림 없으면 함께 생성). 이미 있으면(BUSYGROUP) 무시. */
    private void ensureGroup() {
        try {
            stringRedisTemplate.opsForStream().createGroup(
                    ChatModerationQueueAdapter.STREAM_KEY, ReadOffset.from("0"), GROUP);
        } catch (Exception e) {
            log.debug("consumer group 생성 생략: {}", e.getMessage());
        }
    }
}