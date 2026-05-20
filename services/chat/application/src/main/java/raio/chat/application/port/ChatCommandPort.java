package raio.chat.application.port;

import raio.chat.domain.ChatLogs;

public interface ChatCommandPort {
    ChatLogs save(ChatLogs chatLogs, String senderNickname);
}
