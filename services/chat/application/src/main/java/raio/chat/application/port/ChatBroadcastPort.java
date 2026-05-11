package raio.chat.application.port;

import raio.chat.domain.ChatLogs;

public interface ChatBroadcastPort {
    void broadcastMessage(Long streamId, ChatLogs chatLogs, String senderNickname); // ← 추가
    void broadcastUserJoined(Long streamId, Long userId, String nickname);
    void broadcastUserLeft(Long streamId, Long userId, String nickname);
}