package raio.chat.application.port;

/** 채팅 메시지를 모더레이션 큐에 적재하는 아웃바운드 포트. */
public interface ChatModerationPort {
    void enqueue(String chatId, String streamId, String message);
}