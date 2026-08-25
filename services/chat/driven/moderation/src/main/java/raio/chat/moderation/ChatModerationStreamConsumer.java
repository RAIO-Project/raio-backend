package raio.chat.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 모더레이션 큐 컨슈머. 메시지를 받아 AI 분류 후 혐오면 블라인드 처리.
 * 채팅 전송과 분리된 비동기 워커
 *
 * <p>블라인드는 두 단계로 분리:
 * <ol>
 *   <li>markBlocked — DB 영속 갱신(커맨드, @Transactional 반환 시 커밋)</li>
 *   <li>broadcastBlind — 실시간 통지(커밋 후, 트랜잭션 밖)</li>
 * </ol>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModerationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final ChatModerationBatchWorker batchWorker;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        if(batchWorker.submit(record)) {
            return;
        }

        log.warn("[모더레이션] 버퍼 FULL - 재시도 대기 chatId={}", record.getValue().get("streamId"));
    }
}