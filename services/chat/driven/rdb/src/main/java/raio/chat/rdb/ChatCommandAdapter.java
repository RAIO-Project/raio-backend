package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.domain.ChatLogs;
import raio.chat.rdb.mapper.ChatLogsEntityMapper;
import raio.snowflake.persistence.id.Snowflake;

import static raio.chat.exception.ChatErrorCode.INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class ChatCommandAdapter implements ChatCommandPort {

    private final ChatLogsJpaRepository chatLogsJpaRepository;
    private final ChatLogsEntityMapper chatLogsEntityMapper;
    private final Snowflake snowflake;  // ← 추가

    @Override
    public ChatLogs save(ChatLogs chatLogs, String senderNickname) {
        var entity = chatLogsEntityMapper.toEntity(
                chatLogs,
                senderNickname,
                snowflake.nextId()   // ← id 생성해서 전달
        );
        try {
            var saved = chatLogsJpaRepository.save(entity);
            chatLogsJpaRepository.flush();
            return chatLogsEntityMapper.toDomain(saved);
        } catch (DataAccessException e) {
            throw INTERNAL_ERROR.exception(e);
        }
    }
}