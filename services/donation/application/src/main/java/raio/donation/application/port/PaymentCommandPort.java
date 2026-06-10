package raio.donation.application.port;

/**
 * 후원 시 후원자의 포인트를 차감하는 아웃바운드 포트.
 *
 * <p>실제 구현은 payment 서비스와 gRPC 로 연동 예정(별도 어댑터). 현재는 인터페이스만 정의하고
 * 후원 생성 플로우에 자리를 잡아둔다. 구현체가 생기기 전까지는 NoOp 어댑터로 통과시킨다.
 */
public interface PaymentCommandPort {

    /**
     * 후원 금액만큼 포인트 차감.
     * @param senderId 후원자(차감 대상)
     * @param amount   차감 금액(원/포인트)
     * @return 차감 성공 여부
     */
    boolean deductPoint(Long senderId, Long amount);
}