package raio.chat.application.usecase;

/** 채팅 블라인드: 영속 상태 갱신 + 시청자에게 실시간 블라인드 통지. */
public interface ChatBlindUseCase {
    void blind(String chatId, Long streamId, String reason);
}