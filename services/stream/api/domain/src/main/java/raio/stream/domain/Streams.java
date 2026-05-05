package raio.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.stream.domain.type.StreamStatus;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Streams {
    
    /** 방송 ID (PK) */
    private String id;
    
    /** 스트리머(방송자) ID */
    private String streamerId;
    
    /** 방송 제목 */
    private String title;
    
    /** 카테고리 */
    private String category;
    
    /** 최대 시청자 수 */
    private Integer maxViewerCount;
    
    /** 방송 상태 (READY / LIVE / ENDED) */
    private StreamStatus status;
    
    /** 방송 시작 시간 */
    private Instant startedAt;
    
    /** 방송 종료 시간 */
    private Instant endedAt;
}