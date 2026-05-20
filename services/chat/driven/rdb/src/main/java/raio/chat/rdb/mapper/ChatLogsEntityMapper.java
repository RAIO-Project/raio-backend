package raio.chat.rdb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.chat.domain.ChatLogs;
import raio.chat.rdb.entity.ChatLogsEntity;

@Mapper(componentModel = "spring")
public interface ChatLogsEntityMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "streamId", expression = "java(String.valueOf(entity.getStreamId()))")
    @Mapping(target = "userId", expression = "java(String.valueOf(entity.getUserId()))")
    ChatLogs toDomain(ChatLogsEntity entity);

    @Mapping(target = "id", source = "id")              // ← 파라미터로 받음
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "streamId", expression = "java(Long.parseLong(chatLogs.getStreamId()))")
    @Mapping(target = "userId", expression = "java(Long.parseLong(chatLogs.getUserId()))")
    @Mapping(target = "senderNickname", source = "senderNickname")
    ChatLogsEntity toEntity(ChatLogs chatLogs, String senderNickname, Long id);  // ← id 추가
}