package raio.donation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raio.donation.domain.type.DonationStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donations {
    
    /**
     * 후원 ID (PK)
     */
    private Long id;
    
    /**
     * 방송 ID
     */
    private Long streamId;
    
    /**
     * 후원자(시청자) ID
     */
    private Long senderId;
    
    /**
     * 수령자(스트리머) ID
     */
    private Long receiverId;
    
    /**
     * 후원 금액 (원)
     */
    private Long amount;
    
    /**
     * 후원 메시지
     */
    private String message;
    
    /**
     * 욕설/정책 차단 여부
     */
    private boolean isBlocked;
    
    /**
     * 환불 여부
     */
    private boolean isRefunded;
    
    /**
     * 상태 (COMPLETED / FAILED)
     */
    private DonationStatus status;
}