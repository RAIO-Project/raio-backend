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
    @Mapping(target = "blockedReason", source = "entity.blockReason")
    ChatLogs toDomain(ChatLogsEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "streamId", expression = "java(Long.parseLong(chatLogs.getStreamId()))")
    @Mapping(target = "userId", expression = "java(Long.parseLong(chatLogs.getUserId()))")
    @Mapping(target = "senderNickname", source = "senderNickname")
    @Mapping(target = "blockReason", source = "chatLogs.blockedReason")
    ChatLogsEntity toEntity(ChatLogs chatLogs, String senderNickname);
}