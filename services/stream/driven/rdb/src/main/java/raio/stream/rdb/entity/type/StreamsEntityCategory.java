package raio.stream.rdb.entity.type;

import raio.stream.domain.type.StreamCategory;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static raio.stream.exception.StreamErrorCode.INVALID_STREAM_CATEGORY;

/**
 * 영속 계층 전용 category enum.
 *
 * <p>{@link StreamsEntityStatus} 와 동일한 컨벤션으로 도메인 {@link StreamCategory} 와 분리.
 */
public enum StreamsEntityCategory {
    GAMING,
    MUKBANG,
    TALK,
    MUSIC,
    STUDY,
    SPORTS,
    CREATIVE;

    public static StreamsEntityCategory valueOf(StreamCategory category) {
        assert Set.of(
                        StreamCategory.GAMING,
                        StreamCategory.MUKBANG,
                        StreamCategory.TALK,
                        StreamCategory.MUSIC,
                        StreamCategory.STUDY,
                        StreamCategory.SPORTS,
                        StreamCategory.CREATIVE
                )
                .containsAll(Arrays.stream(StreamCategory.values()).collect(Collectors.toSet()))
                : "StreamCategory 중 일부가 StreamsEntityCategory 인스턴스에 매핑되지 않습니다.";

        return switch (category) {
            case GAMING -> GAMING;
            case MUKBANG -> MUKBANG;
            case TALK -> TALK;
            case MUSIC -> MUSIC;
            case STUDY -> STUDY;
            case SPORTS -> SPORTS;
            case CREATIVE -> CREATIVE;
            default -> throw INVALID_STREAM_CATEGORY.exception();
        };
    }

    public StreamCategory toDomain() {
        return switch (this) {
            case GAMING -> StreamCategory.GAMING;
            case MUKBANG -> StreamCategory.MUKBANG;
            case TALK -> StreamCategory.TALK;
            case MUSIC -> StreamCategory.MUSIC;
            case STUDY -> StreamCategory.STUDY;
            case SPORTS -> StreamCategory.SPORTS;
            case CREATIVE -> StreamCategory.CREATIVE;
        };
    }
}
