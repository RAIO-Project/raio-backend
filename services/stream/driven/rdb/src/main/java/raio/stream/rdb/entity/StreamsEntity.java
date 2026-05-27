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
import raio.stream.rdb.entity.type.StreamsEntityCategory;
import raio.stream.rdb.entity.type.StreamsEntityCategoryConverter;
import raio.stream.rdb.entity.type.StreamsEntityStatus;
import raio.stream.rdb.entity.type.StreamsEntityStatusConverter;

import java.time.Instant;

/**
 * streams 테이블 매핑 엔티티.
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

    @Convert(converter = StreamsEntityCategoryConverter.class)
    @Column(name = "category", length = 50)
    public StreamsEntityCategory category;

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
            StreamsEntityCategory category,
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
