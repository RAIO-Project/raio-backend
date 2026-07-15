package raio.donation.grpc.client;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import raio.donation.application.port.PaymentCommandPort;
import raio.payment.grpc.DonatePointRequest;
import raio.payment.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

/**
 * payment 서비스로의 gRPC Client Adapter ({@link PaymentCommandPort} 구현).
 *
 * <p>지갑 조회·잔액 검증·차감은 payment 의 책임이다. 여기서는 후원자(userId)와 금액만 넘기고,
 * 잔액 부족 등으로 실패하면 gRPC 예외를 받아 false 로 변환한다.
 *
 * <p>필드명은 빈 이름({@code donationWalletStub})과 일치해야 user 의 stub 과 구분되어 주입된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DonationGrpcClientAdapter implements PaymentCommandPort {

    private final WalletCommandServiceBlockingStub donationWalletStub;

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
            // 잔액 부족·지갑 없음 등 payment 측 실패
            log.warn("포인트 차감 실패 - senderId: {}, amount: {}, status: {}",
                    senderId, amount, e.getStatus());
            return false;
        }
    }
}
