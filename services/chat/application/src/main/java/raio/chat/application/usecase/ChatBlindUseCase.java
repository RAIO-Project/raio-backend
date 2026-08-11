package raio.chat.application.usecase;

public interface ChatBlindUseCase {
    /**
     * @return 이 블라인드로 누적 위반이 임계치를 넘어 블랙리스트 처리까지 됐는지 여부
     */
    boolean markBlocked(String chatId, String reason);
}
