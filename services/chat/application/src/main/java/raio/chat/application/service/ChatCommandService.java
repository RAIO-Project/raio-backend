package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatSendUseCase {

    private final ChatCommandPort chatCommandPort;
    private final ChatBroadcastPort chatBroadcastPort;

    @Override
    public ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname) {
        // 1. DB 저장
        var saved = chatCommandPort.save(chatLogs, senderNickname);

        // 2. WebSocket broadcast
        chatBroadcastPort.broadcastMessage(
                saved.getStreamId() != null ? Long.parseLong(saved.getStreamId()) : null,
                saved,
                senderNickname
        );
        return saved;
    }
}