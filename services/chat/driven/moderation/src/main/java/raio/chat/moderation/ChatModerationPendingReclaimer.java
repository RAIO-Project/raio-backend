package raio.chat.moderation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands.XClaimOptions;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 미처리 모더레이션 메시지 회수기.
 *
 * <p>컨슈머가 판정에 실패하면 ack 하지 않으므로 메시지는 PEL 에 남는다. PEL 에만 있는 메시지는
 * 그 컨슈머에게 배정된 상태라 다른 워커가 가져가지 못하고, 아무도 손대지 않으면 영영 방치된다.
 * 그래서 일정 시간 이상 묵은 메시지를 주기적으로 회수해 다시 처리 대상으로 만든다.

 * <p>같은 메시지가 계속 실패하면 무한히 돌 수 있으므로, 전달 횟수가 상한을
 * 넘으면 ack 해서 큐에서 제거하고 로그로 남긴다.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModerationPendingReclaimer {

    /** 회수한 메시지를 처리할 컨슈머 이름. 워커 이름과 겹치지 않게 분리한다. */
    private static final String RECLAIMER_CONSUMER = "moderation-reclaimer";

    private final StringRedisTemplate stringRedisTemplate;
    private final ChatModerationStreamConsumer consumer;
    private final ChatModerationQueueProperties properties;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "moderation-reclaimer");
            t.setDaemon(true);
            return t;
        });

        long intervalMillis = properties.claimInterval().toMillis();
        scheduler.scheduleWithFixedDelay(
                this::reclaimQuietly, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);

        log.info("[모더레이션 회수] 시작 - 주기={}, 최소유휴={}, 최대전달={}회",
                properties.claimInterval(), properties.claimMinIdle(), properties.maxDeliveryCount());
    }

    /** 스케줄러 스레드가 예외로 죽지 않도록 감싼다 (scheduleWithFixedDelay 는 예외 시 중단된다). */
    private void reclaimQuietly() {
        try {
            reclaim();
        } catch (Exception e) {
            log.warn("[모더레이션 회수] 실패 - 다음 주기에 재시도", e);
        }
    }

    private void reclaim() {
        PendingMessages pending = stringRedisTemplate.opsForStream().pending(
                ChatModerationQueueAdapter.STREAM_KEY,
                ChatModerationQueueAdapter.CONSUMER_GROUP,
                Range.unbounded(),
                properties.claimBatchSize());

        if (pending == null || pending.isEmpty()) {
            return;
        }

        List<RecordId> toClaim = new ArrayList<>();

        for (PendingMessage message : pending) {
            // 아직 처리 중일 수 있는 메시지는 건드리지 않는다.
            if (message.getElapsedTimeSinceLastDelivery().compareTo(properties.claimMinIdle()) < 0) {
                continue;
            }

            if (message.getTotalDeliveryCount() >= properties.maxDeliveryCount()) {
                dropPoisonMessage(message);
                continue;
            }

            toClaim.add(message.getId());
        }

        if (toClaim.isEmpty()) {
            return;
        }

        reprocess(toClaim);
    }

    /**
     * 회수 후 재처리. 컨슈머를 직접 호출하므로 성공 시 컨슈머가 ack 하고,
     * 실패하면 다시 PEL 에 남아 다음 주기에 또 회수된다.
     */
    private void reprocess(List<RecordId> ids) {
        StreamOperations<String, String, String> streamOps = stringRedisTemplate.opsForStream();

        List<MapRecord<String, String, String>> claimed = streamOps.claim(
                ChatModerationQueueAdapter.STREAM_KEY,
                ChatModerationQueueAdapter.CONSUMER_GROUP,
                RECLAIMER_CONSUMER,
                XClaimOptions.minIdle(properties.claimMinIdle())
                        .ids(ids.toArray(new RecordId[0])));

        if (claimed == null || claimed.isEmpty()) {
            return;
        }

        log.info("[모더레이션 회수] {}건 재처리", claimed.size());
        for (MapRecord<String, String, String> record : claimed) {
            consumer.onMessage(record);
        }
    }

    /**
     * 반복 실패한 메시지는 ack 해서 큐에서 뺀다. 남겨두면 회수 배치를 계속 차지해
     * 정상 메시지의 재처리까지 늦춘다.
     */
    private void dropPoisonMessage(PendingMessage message) {
        log.warn("[모더레이션 회수] 전달 {}회 초과 - 판정 포기 recordId={}",
                message.getTotalDeliveryCount(), message.getId());

        stringRedisTemplate.opsForStream().acknowledge(
                ChatModerationQueueAdapter.STREAM_KEY,
                ChatModerationQueueAdapter.CONSUMER_GROUP,
                message.getId());
    }

    @PreDestroy
    void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
