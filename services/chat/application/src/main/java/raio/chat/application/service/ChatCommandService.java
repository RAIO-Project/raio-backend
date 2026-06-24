package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.application.port.ChatModerationPort;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatSendUseCase {

    private final ChatCommandPort chatCommandPort;
    private final ChatBroadcastPort chatBroadcastPort;
    private final ChatModerationPort chatModerationPort;

    @Override
    public ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname) {
        // TODO(정규식 1차 필터): AI 전에 금칙어/정규식으로 명백 위반 사전 차단.
        //   명백 위반이면 여기서 차단 표시 + 브로드캐스트 스킵(한순간도 안 보이게).
        //   애매한 건 통과시켜 아래 AI 비동기 모더레이션에 맡긴다.
        // if (profanityFilter.isObviouslyHate(chatLogs.getMessage())) { ... return blocked; }

        // 1. DB 저장
        var saved = chatCommandPort.save(chatLogs, senderNickname);

        // 2. WebSocket broadcast (즉시 표시)
        Long streamId = saved.getStreamId() != null ? Long.parseLong(saved.getStreamId()) : null;
        chatBroadcastPort.broadcastMessage(streamId, saved, senderNickname);

        // 3. AI 모더레이션 큐 적재 (비동기 — 사후 블라인드). 핫패스를 막지 않는다.
        if (saved.getId() != null && streamId != null) {
            chatModerationPort.enqueue(saved.getId(), saved.getStreamId(), saved.getMessage());
        }
        return saved;
    }
}