package raio.stream.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import raio.jpa.support.SnowflakeBaseTimeEntity;
import raio.stream.rdb.entity.type.StreamsEntityStatus;
import raio.stream.rdb.entity.type.StreamsEntityStatusConverter;

import java.time.Instant;

/**
 * streams 테이블 매핑 엔티티.
 *
 * <p>[샘플 BoardEntity 컨벤션 적용]
 *  - status 는 {@link StreamsEntityStatus} 로 도메인 enum 과 분리하고 {@code @Convert} 사용.
 *  - public 필드 + {@code @Builder} 생성자 (불변 필드만 빌더로 주입).
 *  - {@code @NoArgsConstructor(access = AccessLevel.PROTECTED)}.
 *
 * <p>[RAIO 메인 컨벤션 유지]
 *  - PK 가 Snowflake 이므로 {@code LongBaseTimeEntity} 대신 {@link SnowflakeBaseTimeEntity} 상속.
 *  - id / createdAt / updatedAt 는 부모에서 상속.
 */
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        schema = "stream",
        name = "streams"
)
public class StreamsEntity extends SnowflakeBaseTimeEntity {

    @Column(name = "streamer_id", nullable = false)
    public Long streamerId;

    @Column(name = "title", nullable = false, length = 255)
    public String title;

    @Column(name = "category", length = 50)
    public String category;

    @Column(name = "max_viewer_count", nullable = false)
    public Integer maxViewerCount;

    @Convert(converter = StreamsEntityStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    public StreamsEntityStatus status;

    @Column(name = "started_at")
    public Instant startedAt;

    @Column(name = "ended_at")
    public Instant endedAt;

    @Builder
    public StreamsEntity(
            Long streamerId,
            String title,
            String category,
            Integer maxViewerCount,
            StreamsEntityStatus status,
            Instant startedAt,
            Instant endedAt
    ) {
        this.streamerId = streamerId;
        this.title = title;
        this.category = category;
        this.maxViewerCount = maxViewerCount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
