package raio.chat.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import raio.chat.ChatReadModels.ModerationResult;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ModerationPort;
import raio.chat.application.usecase.ChatBlindUseCase;

/**
 * 모더레이션 큐 컨슈머. 메시지를 받아 AI 분류 후 혐오면 블라인드 처리.
 * 채팅 전송과 분리된 비동기 워커 — AI 지연이 채팅을 막지 않는다.
 *
 * 블라인드는 두 단계로 분리:
 *  1) markBlocked  — DB 영속 갱신(커맨드, @Transactional 반환 시 커밋)
 *  2) broadcastBlind — 실시간 통지(커밋 후, 트랜잭션 밖)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatModerationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final ModerationPort moderationPort;
    private final ChatBlindUseCase chatBlindCommand;
    private final ChatBroadcastPort chatBroadcastPort;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        var values = record.getValue();
        var chatId = values.get("chatId");
        var streamId = values.get("streamId");
        var message = values.get("message");

        ModerationResult result = moderationPort.classify(chatId, message);
        if (result.isHate()) {
            String reason = String.join(",", result.hateLabels());
            // 1) 영속 갱신 (커밋)
            chatBlindCommand.markBlocked(chatId, reason);
            // 2) 커밋 후 실시간 통지
            chatBroadcastPort.broadcastBlind(Long.parseLong(streamId), chatId, reason);
            log.debug("채팅 블라인드 - chatId: {}, streamId: {}, reason: {}", chatId, streamId, reason);
        }
        // ack 는 컨테이너 설정(autoAck)에 따름
    }
}
