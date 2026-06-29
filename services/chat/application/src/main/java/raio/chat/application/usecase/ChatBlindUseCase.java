package raio.chat.application.usecase;

public interface ChatBlindUseCase {
    void markBlocked(String chatId, String reason);
}
