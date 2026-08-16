package raio.chat.moderation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 모더레이션 큐 소비 설정.
 *
 * <p>{@code workerCount} 는 Redis Stream 을 폴링하는 백엔드 측 스레드 수다. HF Space 가
 * CPU basic 단일 컨테이너인 이상, 이 값을 늘려도 HF 쪽 처리량은 늘지 않는다.
 * 오히려 제한된 CPU 를 동시 요청끼리 나눠 쓰면서 개별 응답이 더 느려질 위험이 있다.
 *
 * <p>그래서 기본값은 1이다. HF 를 GPU/유료 티어나 다중 인스턴스로 옮기기 전까지는 1을
 * 유지하고, 그 이후에 병렬 여지를 실측하며 올리는 게 맞다. 처리량 자체를 올리려면 워커 수가
 * 아니라 호출 횟수를 줄여야 한다(정규식 선필터, 배치 분류).
 */
@ConfigurationProperties(prefix = "app.chat.moderation.queue")
public record ChatModerationQueueProperties(
        int workerCount,

        // 이 시간 이상 ack 되지 않은 메시지를 미처리로 보고 회수
        Duration claimMinIdle,

        // 회수 주기.
        Duration claimInterval,

        // 한 번에 회수할 최대 건수. 스파이크 이후 회수가 큐를 다시 막지 않게 제한한다.
        int claimBatchSize,

        // 이 횟수를 넘겨도 판정에 실패하면 포기하고 ack.
        int maxDeliveryCount
) {
    private static final int DEFAULT_WORKER_COUNT = 1;
    private static final Duration DEFAULT_CLAIM_MIN_IDLE = Duration.ofSeconds(30);
    private static final Duration DEFAULT_CLAIM_INTERVAL = Duration.ofSeconds(15);
    private static final int DEFAULT_CLAIM_BATCH_SIZE = 50;
    private static final int DEFAULT_MAX_DELIVERY_COUNT = 5;

    public ChatModerationQueueProperties {
        workerCount = workerCount > 0 ? workerCount : DEFAULT_WORKER_COUNT;
        claimMinIdle = claimMinIdle != null ? claimMinIdle : DEFAULT_CLAIM_MIN_IDLE;
        claimInterval = claimInterval != null ? claimInterval : DEFAULT_CLAIM_INTERVAL;
        claimBatchSize = claimBatchSize > 0 ? claimBatchSize : DEFAULT_CLAIM_BATCH_SIZE;
        maxDeliveryCount = maxDeliveryCount > 0 ? maxDeliveryCount : DEFAULT_MAX_DELIVERY_COUNT;
    }
}
