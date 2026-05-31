package raio.stream.rdb.entity.type;

import raio.stream.domain.type.StreamCategory;

import static raio.stream.exception.StreamErrorCode.INVALID_STREAM_CATEGORY;

public enum StreamsEntityCategory {
    GAMING(1),
    MUKBANG(2),
    TALK(3),
    MUSIC(4),
    STUDY(5),
    SPORTS(6),
    CREATIVE(7);

    private final int code;

    StreamsEntityCategory(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StreamsEntityCategory fromCode(int code) {
        return switch (code) {
            case 1 -> GAMING;
            case 2 -> MUKBANG;
            case 3 -> TALK;
            case 4 -> MUSIC;
            case 5 -> STUDY;
            case 6 -> SPORTS;
            case 7 -> CREATIVE;
            default -> throw INVALID_STREAM_CATEGORY.exception();
        };
    }

    public static StreamsEntityCategory valueOf(StreamCategory category) {
        return switch (category) {
            case GAMING -> GAMING;
            case MUKBANG -> MUKBANG;
            case TALK -> TALK;
            case MUSIC -> MUSIC;
            case STUDY -> STUDY;
            case SPORTS -> SPORTS;
            case CREATIVE -> CREATIVE;
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
