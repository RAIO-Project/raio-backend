package raio.stream.rdb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import raio.stream.domain.Streams;
import raio.stream.rdb.entity.StreamsEntity;
import raio.stream.rdb.entity.type.StreamsEntityCategory;
import raio.stream.rdb.entity.type.StreamsEntityStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/**
 * 도메인/엔티티/리드모델 매핑.
 * status/category 는 도메인/리드모델은 도메인 enum, 엔티티는 영속 enum(코드 저장).
 */
@Mapper(componentModel = "spring")
public interface StreamsEntityMapper {

    // ----- 조회 -----
    @Mapping(target = "status", expression = "java(entity.status.toDomain())")
    @Mapping(target = "category", expression = "java(entity.category == null ? null : entity.category.toDomain())")
    LiveStreamSummary toLiveStreamSummary(StreamsEntity entity);

    @Mapping(target = "id", expression = "java(entity.getId())")
    @Mapping(target = "status", expression = "java(entity.status.toDomain())")
    @Mapping(target = "category", expression = "java(entity.category == null ? null : entity.category.toDomain())")
    StreamDetail toStreamDetail(StreamsEntity entity);

    // ----- 신규 방송 저장용: 도메인 -> 엔티티 -----
    @Mapping(target = "streamerId", expression = "java(Long.parseLong(stream.getStreamerId()))")
    @Mapping(target = "category", expression = "java(stream.getCategory() == null ? null : StreamsEntityCategory.valueOf(stream.getCategory()))")
    @Mapping(target = "status", expression = "java(StreamsEntityStatus.valueOf(stream.getStatus()))")
    StreamsEntity toEntity(Streams stream);

    // ----- 엔티티 -> 도메인 -----
    @Mapping(target = "id", expression = "java(String.valueOf(entity.getId()))")
    @Mapping(target = "streamerId", expression = "java(String.valueOf(entity.streamerId))")
    @Mapping(target = "status", expression = "java(entity.status.toDomain())")
    @Mapping(target = "category", expression = "java(entity.category == null ? null : entity.category.toDomain())")
    Streams toDomain(StreamsEntity entity);

    /**
     * 상태 전이 결과를 기존 엔티티에 반영 (update 용).
     * id/streamerId/title/category 등 불변 필드는 건드리지 않는다.
     */
    default void applyDomain(Streams stream, @MappingTarget StreamsEntity entity) {
        entity.status = StreamsEntityStatus.valueOf(stream.getStatus());
        entity.startedAt = stream.getStartedAt();
        entity.endedAt = stream.getEndedAt();
        entity.maxViewerCount = stream.getMaxViewerCount();
    }
}
