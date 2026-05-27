package raio.stream.rdb.entity.type;

import raio.stream.domain.type.StreamStatus;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static raio.stream.exception.StreamErrorCode.INVALID_STREAM_STATUS;

/**
 * 영속 계층 전용 status enum.
 *
 * <p>샘플 컨벤션 ({@code BoardEntityStatus}) 을 따라 도메인 {@link StreamStatus} 와 분리한다.
 *
 * <p>[샘플과의 차이]
 *  - 샘플은 {@code StatusParameters} 기반 비트 코드(int) 로 컬럼에 저장하지만,
 *    {@code stream.streams.status} 컬럼은 VARCHAR(20) 으로 정의되어 있어
 *    여기서는 enum {@code name()} 그대로 저장한다.
 *  - DB 스키마가 비트 코드로 전환되면 {@code BoardEntityStatus} 식의 분기로 보강.
 */
public enum StreamsEntityStatus {
    READY,
    LIVE,
    ENDED;

    public static StreamsEntityStatus valueOf(StreamStatus status) {
        assert Set.of(StreamStatus.READY, StreamStatus.LIVE, StreamStatus.ENDED)
                .containsAll(Arrays.stream(StreamStatus.values()).collect(Collectors.toSet()))
                : "StreamStatus 중 일부가 StreamsEntityStatus 인스턴스에 매핑되지 않습니다.";

        return switch (status) {
            case READY -> READY;
            case LIVE -> LIVE;
            case ENDED -> ENDED;
            default -> throw INVALID_STREAM_STATUS.exception();
        };
    }

    public StreamStatus toDomain() {
        return switch (this) {
            case READY -> StreamStatus.READY;
            case LIVE -> StreamStatus.LIVE;
            case ENDED -> StreamStatus.ENDED;
        };
    }
}
