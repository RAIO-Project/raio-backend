package raio.donation.application.port;

/**
 * 후원 시 후원자의 포인트를 조작하는 아웃바운드 포트. wallet 서비스와 gRPC 로 연동한다.
 *
 * <p>지갑 조회·잔액 검증·증감은 wallet 의 책임이다. 여기서는 후원자(userId)와 금액만 넘기고,
 * 실패하면 gRPC 예외를 받아 false 로 변환한다.
 */
public interface WalletCommandPort {

    /**
     * 후원 금액만큼 포인트 차감.
     *
     * @param senderId 후원자(차감 대상)
     * @param amount   차감 금액(원/포인트)
     * @return 차감 성공 여부
     */
    boolean deductPoint(Long senderId, Long amount);

    /**
     * 차감된 포인트를 되돌린다 (보상 트랜잭션).
     *
     * <p>포인트 차감은 wallet 서비스에서 이미 커밋된 상태이므로, 이후 단계가 실패해도 후원 서비스의
     * 로컬 트랜잭션 롤백으로는 되돌릴 수 없다. 반드시 역방향 호출로 복구해야 한다.
     *
     * @param senderId 후원자
     * @param amount   되돌릴 금액
     * @param sourceId 멱등키. 같은 값으로 여러 번 호출돼도 복구는 한 번만 반영된다.
     * @return 복구 성공 여부. false 면 포인트가 빠진 채로 남아 수동 개입이 필요하다.
     */
    boolean refundPoint(Long senderId, Long amount, String sourceId);
}
