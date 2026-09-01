package raio.donation.grpc.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import raio.donation.application.port.WalletCommandPort;
import raio.wallet.grpc.ChargePointsRequest;
import raio.wallet.grpc.DonatePointRequest;
import raio.wallet.grpc.GetWalletRequest;
import raio.wallet.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

/**
 * wallet 서비스로의 gRPC Client Adapter ({@link WalletCommandPort} 구현).
 *
 * <p>지갑 조회·잔액 검증·증감은 wallet 의 책임이다. 여기서는 후원자(userId)와 금액만 넘기고,
 * 잔액 부족 등으로 실패하면 gRPC 예외를 받아 false 로 변환한다.
 *
 * <p>필드명은 빈 이름({@code donationWalletStub})과 일치해야 user 의 stub 과 구분되어 주입된다.
 */
@Slf4j
@Component
public class DonationGrpcClientAdapter implements WalletCommandPort {

    private final WalletCommandServiceBlockingStub donationWalletStub;

    public DonationGrpcClientAdapter(@Qualifier("donationWalletStub") WalletCommandServiceBlockingStub donationWalletStub) {
        this.donationWalletStub = donationWalletStub;
    }

    @Override
    public boolean deductPoint(Long senderId, Long amount) {
        try {
            var response = donationWalletStub.donatePoint(
                    DonatePointRequest.newBuilder()
                            .setUserId(String.valueOf(senderId))
                            .setAmount(amount)
                            .build()
            );
            log.debug("포인트 차감 완료 - senderId: {}, amount: {}, balance: {}",
                    senderId, amount, response.getBalance());
            return true;
        } catch (StatusRuntimeException e) {
            // 잔액 부족·지갑 없음 등 wallet 측 실패
            log.warn("포인트 차감 실패 - senderId: {}, amount: {}, status: {}",
                    senderId, amount, e.getStatus());
            return false;
        }
    }

    /**
     * 차감 복구.
     *
     * <p>{@code sourceId} 는 wallet 이 (지갑, 유형, sourceId) 조합으로 중복을 걸러내는 멱등키다.
     * 같은 키로 재호출해도 복구가 두 번 반영되지 않는다.
     */
    @Override
    public boolean refundPoint(Long senderId, Long amount, String sourceId) {
        String walletId;
        try {
            walletId = donationWalletStub.getWallet(
                    GetWalletRequest.newBuilder()
                            .setUserId(String.valueOf(senderId))
                            .build()
            ).getWalletId();
        } catch (StatusRuntimeException e) {
            log.error("포인트 복구용 지갑 조회 실패 - senderId: {}, amount: {}, sourceId: {}, status: {}",
                    senderId, amount, sourceId, e.getStatus());
            return false;
        }

        try {
            var response = donationWalletStub.chargePoints(
                    ChargePointsRequest.newBuilder()
                            .setWalletId(walletId)
                            .setAmount(amount)
                            .setSourceId(sourceId)
                            .build()
            );
            log.info("포인트 복구 완료 - senderId: {}, walletId: {}, amount: {}, sourceId: {}, balance: {}",
                    senderId, walletId, amount, sourceId, response.getBalance());
            return true;
        } catch (StatusRuntimeException e) {
            log.error("포인트 복구 실패 - senderId: {}, walletId: {}, amount: {}, sourceId: {}, status: {}",
                    senderId, walletId, amount, sourceId, e.getStatus());
            return false;
        }
    }
}
