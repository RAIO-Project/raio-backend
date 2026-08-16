package raio.chat.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import raio.chat.ChatReadModels.ModerationResult;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ModerationPort;
import raio.chat.application.usecase.ChatBlindUseCase;

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

    private final ModerationPort moderationPort;
    private final ChatBlindUseCase chatBlindCommand;
    private final ChatBroadcastPort chatBroadcastPort;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        var values = record.getValue();
        var chatId = values.get("chatId");
        var streamId = values.get("streamId");
        var message = values.get("message");

        try {
            moderate(chatId, streamId, message);
        } catch (RuntimeException e) {
            // ack 하지 않고 종료 → PEL 에 남아 재시도된다.
            log.warn("[모더레이션] 판정 실패 - 재시도 대기 chatId={}, cause={}", chatId, e.toString());
            return;
        }

        acknowledge(record);
    }

    /**
     * 판정 후 혐오면 블라인드/블랙리스트 처리. 이 메서드가 예외 없이 끝나야 ack 대상이 된다.
     *
     * <p>{@code markBlocked} 이후 {@code broadcastBlind} 가 실패하면 메시지가 재시도되어
     * 처리가 한 번 더 수행될 수 있다. markBlocked 는 is_blocked 를 true 로 덮는 연산이고
     * 블랙리스트 판정도 누적 위반 횟수 기준이라 재수행해도 결과가 같으므로 재시도가 안전하다.
     */
    private void moderate(String chatId, String streamId, String message) {
        ModerationResult result = moderationPort.classify(chatId, message);
        if (!result.isHate()) {
            return;
        }

        String reason = String.join(",", result.hateLabels());

        // 누적 위반 임계치 초과 시 블랙리스트까지 함께 처리되며, 그 여부를 반환값으로 알려준다.
        boolean blacklisted = chatBlindCommand.markBlocked(chatId, reason);
        chatBroadcastPort.broadcastBlind(Long.parseLong(streamId), chatId, reason);

        if (blacklisted) {
            log.warn("채팅 블랙리스트 처리 - chatId: {}, streamId: {}, reason: {}", chatId, streamId, reason);
        } else {
            log.debug("채팅 블라인드 - chatId: {}, streamId: {}, reason: {}", chatId, streamId, reason);
        }
    }

    /**
     * ack 자체가 실패해도 처리 결과는 유지된다. 회수기가 다시 태우겠지만 블라인드는
     * 멱등이므로 중복 수행이 문제되지 않는다.
     */
    private void acknowledge(MapRecord<String, String, String> record) {
        try {
            stringRedisTemplate.opsForStream().acknowledge(
                    ChatModerationQueueAdapter.CONSUMER_GROUP, record);
        } catch (RuntimeException e) {
            log.warn("[모더레이션] ack 실패 - recordId={}", record.getId(), e);
        }
    }
}
