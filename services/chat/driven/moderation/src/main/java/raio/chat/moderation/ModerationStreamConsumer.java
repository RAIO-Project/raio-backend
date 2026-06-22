package raio.chat.moderation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import raio.chat.application.result.ModerationResult;
import raio.chat.application.port.AiModerationPort;
import raio.chat.application.usecase.ChatBlindUseCase;

/**
 * 모더레이션 큐 컨슈머. 메시지를 받아 AI 분류 후 혐오면 블라인드 처리.
 * 채팅 전송와 분리된 비동기 워커 — AI 지연이 채팅을 막지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final AiModerationPort aiModerationPort;
    private final ChatBlindUseCase chatBlindUseCase;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        var values = record.getValue();
        var chatId = values.get("chatId");
        var streamId = values.get("streamId");
        var message = values.get("message");

        ModerationResult result = aiModerationPort.classify(chatId, message);
        if (result.isHate()) {
            chatBlindUseCase.blind(chatId, Long.parseLong(streamId), String.join(",", result.hateLabels()));
        }
        // ack 는 컨테이너 설정(autoAck)에 따름
    }
}