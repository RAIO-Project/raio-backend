package raio.donation.grpc.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import raio.donation.application.port.PaymentCommandPort;

/**
 * payment 서비스로의 gRPC Client Adapter ({@link PaymentCommandPort} 구현).
 * user 도메인의 UserGrpcClientAdapter 와 동일한 구조(포트 구현 → gRPC stub 호출)를 따른다.
 *
 * <p>현재는 gRPC 통신 틀만 잡아두고 실제 호출은 미구현(정상 통과 처리). payment 에 포인트 차감
 * RPC 가 추가되면 stub 호출로 교체한다.
 *
 * <p>TODO(gRPC):
 *  - payment-api/proto 에 포인트 차감 RPC 계약 정의 (예: DeductPointRequest/Response)
 *  - 생성된 WalletCommandServiceBlockingStub 주입 (ManagedChannel 설정)
 *  - deductPoint() 에서 Request 변환 → stub 호출 → Response 로 성공/실패 반환
 */
@Slf4j
@Component
public class DonationGrpcClientAdapter implements PaymentCommandPort {

    // TODO(gRPC): private final WalletCommandServiceBlockingStub walletStub;

    @Override
    public boolean deductPoint(Long senderId, Long amount) {
        // TODO(gRPC): payment 차감 RPC 호출로 교체
        //   var request = DeductPointRequest.newBuilder()
        //           .setUserId(senderId).setAmount(amount).build();
        //   var response = walletStub.deductPoint(request);
        //   return response.getSuccess();
        log.warn("[TODO] gRPC 포인트 차감 미구현 - 정상 통과 처리 (senderId={}, amount={})", senderId, amount);
        return true;
    }
}