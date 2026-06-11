package raio.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import raio.stream.domain.type.VideoStatus;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Videos {

    private Long id;
    private Long uploaderId;
    private String title;
    private String originalFileName;
    private String storedFilePath;
    private Long fileSize;
    private String contentType;
    private VideoStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
