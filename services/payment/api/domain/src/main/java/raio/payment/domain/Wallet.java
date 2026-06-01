package raio.payment.domain;

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
public class Wallet {

    /** 지갑 식별자(PK) */
    private String id;

    /** 소유자 사용자 ID. 한 사용자는 하나의 지갑만 가진다. */
    private String userId;

    /** 현재 포인트 잔액 */
    private Long balance;

    /** 생성 일시 */
    private Instant createdAt;

    /** 마지막 잔액 변경 일시 */
    private Instant updatedAt;
}
