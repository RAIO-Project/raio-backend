package raio.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Blacklist {
    
    /** 블랙리스트 ID (PK) */
    private String id;
    
    /** 대상 사용자 ID */
    private String userId;
    
    /** 차단 사유 */
    private String reason;
    
    /** 차단 해제 예정 시각 */
    private Instant unblockAt;
}