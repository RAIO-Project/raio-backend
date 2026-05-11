package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatSendUseCase {

    private final ChatBroadcastPort chatBroadcastPort;
    // NOTE: ChatCommandPort (DB 저장)
    //       지금은 broadcast만 처리

    @Override
    public ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname) {
        chatBroadcastPort.broadcastMessage(
                chatLogs.getStreamId() != null ? Long.parseLong(chatLogs.getStreamId()) : null,
                chatLogs,
                senderNickname  // ← 흘려보내기
        );
        return chatLogs;
    }
}