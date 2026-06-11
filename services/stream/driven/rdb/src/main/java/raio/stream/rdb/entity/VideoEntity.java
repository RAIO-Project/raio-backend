package raio.stream.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import raio.jpa.support.SnowflakeBaseTimeEntity;
import raio.stream.domain.Videos;
import raio.stream.domain.type.VideoStatus;

@Entity
@Table(name = "videos", schema = "stream")
@SuperBuilder
@NoArgsConstructor
@Getter
public class VideoEntity extends SnowflakeBaseTimeEntity {

    @Column(nullable = false)
    private Long uploaderId;

    @Column(length = 200)
    private String title;

    @Column(nullable = false, length = 255)
    private String originalFileName;

    @Column(nullable = false, length = 500)
    private String storedFilePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoStatus status;

    public Videos toDomain() {
        return Videos.builder()
                .id(getId())
                .uploaderId(uploaderId)
                .title(title)
                .originalFileName(originalFileName)
                .storedFilePath(storedFilePath)
                .fileSize(fileSize)
                .contentType(contentType)
                .status(status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }

    public static VideoEntity from(Videos video) {
        return VideoEntity.builder()
                .uploaderId(video.getUploaderId())
                .title(video.getTitle())
                .originalFileName(video.getOriginalFileName())
                .storedFilePath(video.getStoredFilePath())
                .fileSize(video.getFileSize())
                .contentType(video.getContentType())
                .status(video.getStatus())
                .build();
    }
}
