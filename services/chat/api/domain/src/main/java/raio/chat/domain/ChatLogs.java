package raio.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLogs {
    
    /** 채팅 로그 ID (PK) */
    private String id;
    
    /** 방송 ID */
    private String streamId;
    
    /** 사용자 ID */
    private String userId;
    
    /** 채팅 메시지 */
    private String message;
    
    /** 차단 여부 */
    private boolean isBlocked;
    
    /** 차단 사유 */
    private String blockedReason;

    /**
     * 차단 처리.
     */
    public void blind(String reason) {
        this.isBlocked = true;
        this.blockedReason = reason;
    }
}