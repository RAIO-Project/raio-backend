package raio.chat.moderation;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.ChatReadModels.ModerationResult;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ModerationPort;
import raio.chat.application.port.ModerationPort.ModerationRequestItem;
import raio.chat.application.usecase.ChatBlindUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModerationBatchWorker {
    private final ModerationPort moderationPort;
    private final ChatBlindUseCase chatBlindCommand;
    private final ChatBroadcastPort chatBroadcastPort;
    private final StringRedisTemplate stringRedisTemplate;
    private final ChatModerationQueueProperties chatModerationQueueProperties;

    private BlockingQueue<MapRecord<String, String, String>> buffer;
    private ExecutorService workers;
    private volatile boolean running = true;

    @PostConstruct
    void start() {
        buffer = new ArrayBlockingQueue<>(chatModerationQueueProperties.batchQueueCapacity());

        int workerCount = chatModerationQueueProperties.batchWorkerCount();
        workers = Executors.newFixedThreadPool(workerCount, r-> {
            Thread t = new Thread(r,"Moderation-Batch");
            t.setDaemon(true);
            return t;
        });

        for(int i = 0; i < workerCount; i++) {
            workers.submit(this::runLoop);
        }

        log.info("[모더레이션 배치] 시작 - 워커 = {}, 배치크기 = {}, linger= {}", workerCount, chatModerationQueueProperties.batchSize(), chatModerationQueueProperties.batchLinger());
    }

    boolean submit(MapRecord<String, String, String> record) {
        return buffer.offer(record);
    }

    private void runLoop() {
        while(running) {
            try {
                List<MapRecord<String, String, String>> batch = takeBatch();
                if(!batch.isEmpty()) {
                    processBatch(batch);
                }
            }catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }catch (Exception e) {
                log.error("[모더레이션 배치] 처리 중 예외 발생", e);
            }
        }
    }

    private List<MapRecord<String, String, String>> takeBatch() throws InterruptedException {
        List<MapRecord<String, String, String>> batch = new ArrayList<>();

        var first = buffer.poll(1, TimeUnit.SECONDS);
        if(first == null) {
            return batch;
        }
        batch.add(first);

        long lingerNanos = chatModerationQueueProperties.batchLinger().toNanos();
        long deadline = System.nanoTime() + lingerNanos;

        while(batch.size() < chatModerationQueueProperties.batchSize()) {
            long remaining = deadline - System.nanoTime();
            if(remaining <= 0) {
                break;
            }
            var next = buffer.poll(remaining, TimeUnit.NANOSECONDS);
            if(next == null) {
                break;
            }
            batch.add(next);
        }

        return batch;
    }

    private void processBatch(List<MapRecord<String, String, String>> batch) {
        List<ModerationRequestItem> items = batch.stream()
                .map(r -> new ModerationRequestItem(r.getValue().get("chatId"), r.getValue().get("message")))
                .toList();

        Map<String, ModerationResult> results;

        try{
            results = moderationPort.classifyBatch(items);
        }catch (RuntimeException e) {
            log.warn("[모더레이션 배치] 판정 실패 - {}건 재시도 대기, cause = {}", batch.size(), e.toString());
            return;
        }

        int acked = 0;
        for(var record : batch) {
            String chatId = record.getValue().get("chatId");
            ModerationResult result = results.get(chatId);

            if(result == null) {
                log.warn("[모더레이션 배치] 응답 누락 - 재시도 대기 chatId = {}", chatId);
                continue;
            }

            try{
                applyResult(chatId, record.getValue().get("streamId"), result);
            }catch (RuntimeException e) {
                log.warn("[모더레이션 배치] 후처리 실패 - 재시도 대기 chatId = {}, cause = {}", chatId, e.toString());
                continue;
            }

            acknowledge(record);
            acked++;
        }

        log.debug("[모더레이션 배치] {}건 중 {}건 처리 완료", batch.size(), acked);
    }

    private void applyResult(String chatId, String streamId, ModerationResult result) {
        if(!result.isHate()) {
            return;
        }

        String reason = String.join(",", result.hateLabels());

        boolean blacklisted = chatBlindCommand.markBlocked(chatId, reason);
        chatBroadcastPort.broadcastBlind(Long.parseLong(streamId), chatId, reason);

        if(blacklisted) {
            log.warn("채팅 블랙리스트 처리 - chatId = {}, streamId = {}, reason = {}", chatId, streamId, reason);
        }else{
            log.debug("채팅 블라인드 - chatId = {}, streamId = {}, reason = {}", chatId, streamId, reason);
        }
    }

    private void acknowledge(MapRecord<String, String, String> record) {
        try{
            stringRedisTemplate.opsForStream().acknowledge(
                    ChatModerationQueueAdapter.CONSUMER_GROUP, record
            );
        }catch (RuntimeException e) {
            log.warn("[모더레이션 배치] ack 실패 - recordId={}", record.getValue().get("streamId"), e);
        }
    }
}
