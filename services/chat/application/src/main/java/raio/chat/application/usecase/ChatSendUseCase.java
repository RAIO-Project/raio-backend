package raio.chat.application.usecase;

import raio.chat.domain.ChatLogs;

public interface ChatSendUseCase {
    /**
     * @return 저장된 채팅. 발신자가 블랙리스트(활성 차단) 상태면 저장/브로드캐스트 없이 null 반환.
     */
    ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname);
}