package raio.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;

import java.time.Instant;

/**
 * 방송 도메인. 상태 전이 불변식을 이 안에 캡슐화한다.
 *
 * <p>전이 규칙: READY --start--> LIVE --end--> ENDED (역방향/건너뛰기 불가).
 * 서비스는 이 메서드들을 호출만 하고, 전이 검증의 책임은 도메인이 진다.
 *
 * <p>TODO: 검증 실패 시 던지는 예외를 StreamErrorCode 기반으로 교체.
 *  현재는 domain 모듈이 stream-exception 모듈에 의존하지 않아 표준 예외(IllegalState/IllegalArgument)로 임시 처리.
 *  exception 의존을 추가하면 아래 예외들을:
 *    - STREAM_TITLE_REQUIRED   (title 누락)
 *    - STREAM_ALREADY_STARTED  (이미 LIVE)
 *    - STREAM_ALREADY_ENDED    (이미 ENDED)
 *    - STREAM_NOT_ACTIVE       (LIVE 아닌데 종료 시도)
 *  로 교체할 것.
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
            // TODO: throw STREAM_TITLE_REQUIRED.exception();
            throw new IllegalArgumentException("방송 제목은 필수입니다.");
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
            // TODO: throw STREAM_ALREADY_STARTED.exception();
            throw new IllegalStateException("이미 시작된 방송입니다.");
        }
        if (status == StreamStatus.ENDED) {
            // TODO: throw STREAM_ALREADY_ENDED.exception();
            throw new IllegalStateException("이미 종료된 방송입니다.");
        }
        this.status = StreamStatus.LIVE;
        this.startedAt = now;
    }

    /**
     * 방송 종료: LIVE 일 때만 ENDED 로 전이.
     * @param finalMaxViewerCount Redis 에서 동기화된 최종 최고 시청자 수
     */
    public void end(Instant now, int finalMaxViewerCount) {
        if (status != StreamStatus.LIVE) {
            // TODO: throw STREAM_NOT_ACTIVE.exception();
            throw new IllegalStateException("진행 중인 방송이 아닙니다.");
        }
        this.status = StreamStatus.ENDED;
        this.endedAt = now;
        int current = this.maxViewerCount == null ? 0 : this.maxViewerCount;
        this.maxViewerCount = Math.max(current, finalMaxViewerCount);
    }
}