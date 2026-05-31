package raio.stream.rdb.entity.type;

import raio.stream.domain.type.StreamStatus;

import static raio.stream.exception.StreamErrorCode.INVALID_STREAM_STATUS;

/**
 * 영속 계층 전용 status enum.
 *
 * <p>도메인 {@link StreamStatus} 와 분리하고, DB 에는 공통코드 형태의 숫자 코드로 저장한다
 * ({@link StreamsEntityStatusConverter}). 코드 매핑의 단일 출처(SSOT)는 이 enum 의 code 필드다.
 *
 * <p>[설계 메모]
 *  - status 는 READY → LIVE → ENDED 로 상호 배타적인 단일 라이프사이클이므로
 *    common/status 의 비트마스크(StatusParameters) 가 아니라 단순 정수 코드를 쓴다.
 *  - 코드 값은 {@code ordinal()} 이 아니라 명시적으로 고정한다.
 *    enum 선언 순서가 바뀌어도 저장값이 흔들리지 않도록 하기 위함.
 */
public enum StreamsEntityStatus {
    READY(1),
    LIVE(2),
    ENDED(3);

    private final int code;

    StreamsEntityStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StreamsEntityStatus fromCode(int code) {
        return switch (code) {
            case 1 -> READY;
            case 2 -> LIVE;
            case 3 -> ENDED;
            default -> throw INVALID_STREAM_STATUS.exception();
        };
    }

    public static StreamsEntityStatus valueOf(StreamStatus status) {
        return switch (status) {
            case READY -> READY;
            case LIVE -> LIVE;
            case ENDED -> ENDED;
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