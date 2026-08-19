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
 * 모더레이션 컨슈머를 등록한다.
 *
 * <p>{@code receiveAutoAck} 대신 {@code receive} 로 등록해 ack 시점을 컨슈머가 직접 정한다.
 * 판정에 실패한 메시지를 PEL 에 남겨 재시도하기 위해서다.
 *
 * <p>워커는 설정된 개수만큼 서로 다른 컨슈머 이름으로 등록한다. 같은 그룹이므로 메시지는
 * 워커들에게 분배되고 중복 소비되지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModerationStreamSubscriber {

    private static final String CONSUMER_NAME_PREFIX = "moderation-worker-";

    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatModerationStreamConsumer consumer;
    private final ChatModerationQueueProperties properties;

    @PostConstruct
    void registerSubscription() {
        ensureGroup();

        int workerCount = properties.workerCount();
        for (int i = 1; i <= workerCount; i++) {
            String consumerName = CONSUMER_NAME_PREFIX + i;
            container.receive(
                    Consumer.from(ChatModerationQueueAdapter.CONSUMER_GROUP, consumerName),
                    StreamOffset.create(ChatModerationQueueAdapter.STREAM_KEY, ReadOffset.lastConsumed()),
                    consumer);
        }

        log.info("[모더레이션] 컨슈머 {}개 등록", workerCount);
    }

    /** 컨슈머 그룹이 없으면 생성 (스트림 없으면 함께 생성). */
    private void ensureGroup() {
        try {
            stringRedisTemplate.opsForStream().createGroup(
                    ChatModerationQueueAdapter.STREAM_KEY,
                    ReadOffset.from("0"),
                    ChatModerationQueueAdapter.CONSUMER_GROUP);
        } catch (Exception e) {
            log.debug("consumer group 생성 생략: {}", e.getMessage());
        }
    }
}
