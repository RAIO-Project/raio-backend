package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.domain.ChatLogs;
import raio.chat.rdb.mapper.ChatLogsEntityMapper;

import static raio.chat.exception.ChatErrorCode.INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class ChatCommandAdapter implements ChatCommandPort {

    private final ChatLogsJpaRepository chatLogsJpaRepository;
    private final ChatLogsEntityMapper chatLogsEntityMapper;

    @Override
    public ChatLogs save(ChatLogs chatLogs, String senderNickname) {
        var entity = chatLogsEntityMapper.toEntity(chatLogs, senderNickname);
        try {
            var saved = chatLogsJpaRepository.save(entity);
            chatLogsJpaRepository.flush();
            return chatLogsEntityMapper.toDomain(saved);
        } catch (DataAccessException e) {
            throw INTERNAL_ERROR.exception(e);
        }
    }
}