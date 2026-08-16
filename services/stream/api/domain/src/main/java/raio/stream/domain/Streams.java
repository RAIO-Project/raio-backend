package raio.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;

import java.time.Instant;

import static raio.stream.exception.StreamErrorCode.STREAM_ALREADY_ENDED;
import static raio.stream.exception.StreamErrorCode.STREAM_ALREADY_STARTED;
import static raio.stream.exception.StreamErrorCode.STREAM_NOT_ACTIVE;
import static raio.stream.exception.StreamErrorCode.STREAM_TITLE_REQUIRED;

/**
 * 방송 도메인. 상태 전이 불변식을 이 안에 캡슐화한다.
 *
 * <p>전이 규칙: READY --start--> LIVE --end--> ENDED (역방향/건너뛰기 불가).
 * 서비스는 이 메서드들을 호출만 하고, 전이 검증의 책임은 도메인이 진다.
 *
 * <p>불변식 위반은 {@code StreamErrorCode} 로 던진다. 표준 예외(IllegalState/IllegalArgument)로
 * 던지면 전역 핸들러가 매핑하지 못해 클라이언트에게 500 이 나가고, 정상적인 사용자 실수
 * (이미 시작된 방송을 다시 시작 등)가 서버 오류로 집계된다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Streams {

    private String id;
    private String streamerId;
    private String title;
    private StreamCategory category;
    private Integer maxViewerCount;
    private StreamStatus status;
    private Instant startedAt;
    private Instant endedAt;

    /** 신규 방송 생성 (READY). */
    public static Streams create(String streamerId, String title, StreamCategory category) {
        if (title == null || title.isBlank()) {
            throw STREAM_TITLE_REQUIRED.exception();
        }
        return Streams.builder()
                .streamerId(streamerId)
                .title(title)
                .category(category)
                .maxViewerCount(0)
                .status(StreamStatus.READY)
                .build();
    }

    /** 방송 시작: READY 일 때만 LIVE 로 전이. */
    public void start(Instant now) {
        if (status == StreamStatus.LIVE) {
            throw STREAM_ALREADY_STARTED.exception();
        }
        if (status == StreamStatus.ENDED) {
            throw STREAM_ALREADY_ENDED.exception();
        }
        this.status = StreamStatus.LIVE;
        this.startedAt = now;
    }

    /**
     * 방송 종료: LIVE 일 때만 ENDED 로 전이.
     *
     * @param finalMaxViewerCount Redis 에서 동기화된 최종 최고 시청자 수
     */
    public void end(Instant now, int finalMaxViewerCount) {
        if (status == StreamStatus.ENDED) {
            throw STREAM_ALREADY_ENDED.exception();
        }
        if (status != StreamStatus.LIVE) {
            throw STREAM_NOT_ACTIVE.exception();
        }
        this.status = StreamStatus.ENDED;
        this.endedAt = now;
        int current = this.maxViewerCount == null ? 0 : this.maxViewerCount;
        this.maxViewerCount = Math.max(current, finalMaxViewerCount);
    }
}
