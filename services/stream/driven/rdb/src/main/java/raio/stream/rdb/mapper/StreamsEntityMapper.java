package raio.stream.rdb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.stream.rdb.entity.StreamsEntity;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

@Mapper(componentModel = "spring")
public interface StreamsEntityMapper {

    @Mapping(target = "status", expression = "java(entity.status.toDomain())")
    LiveStreamSummary toLiveStreamSummary(StreamsEntity entity);
}
