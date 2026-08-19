package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.ChatQueryPort;

@Repository
@RequiredArgsConstructor
public class ChatQueryAdapter implements ChatQueryPort {

    private final ChatLogsJpaRepository chatLogsJpaRepository;

    @Override
    public String findUserIdById(String chatId) {
        return String.valueOf(chatLogsJpaRepository.findUserIdById(Long.parseLong(chatId)));
    }

    @Override
    public long countBlockedByUserId(String userId) {
        return chatLogsJpaRepository.countBlockedByUserId(Long.parseLong(userId));
    }
}
