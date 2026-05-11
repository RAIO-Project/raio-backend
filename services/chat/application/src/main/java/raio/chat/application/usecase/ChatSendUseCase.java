package raio.chat.application.usecase;

import raio.chat.domain.ChatLogs;

public interface ChatSendUseCase {
    ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname);
}