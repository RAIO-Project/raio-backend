package raio.chat.rdb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.chat.domain.Blacklist;
import raio.chat.rdb.entity.BlacklistEntity;

@Mapper(componentModel = "spring")
public interface BlacklistEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", expression = "java(Long.parseLong(blacklist.getUserId()))")
    BlacklistEntity toEntity(Blacklist blacklist);
}
