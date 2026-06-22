package raio.donation.grpc.client.config;

import org.springframework.context.annotation.Configuration;

/**
 * donation → payment gRPC Client 설정.
 * user 의 PaymentGrpcClientConfig 패턴을 따른다(공통 GrpcClientConfig.createChannel 사용 + stub 빈).
 *
 * <p>현재는 틀만 둔다. donation 이 필요한 RPC 는 "포인트 차감"인데 payment proto 에 아직 차감 RPC 가
 * 없어서 stub 을 만들 수 없다. 차감 RPC 가 추가되면 아래 TODO 를 활성화한다.
 *
 * <p>주의: user 가 이미 WalletCommandServiceBlockingStub(지갑 생성) 빈을 등록한다. donation 은
 * 같은 stub 을 중복 등록하면 안 되고, 차감 전용 RPC stub(예: WalletPointServiceBlockingStub)을 별도로 만든다.
 *
 * <p>TODO(gRPC):
 *  @Bean(destroyMethod = "shutdown")
 *  ManagedChannel paymentManagedChannel(GrpcClientConfig grpcClientConfig) {
 *      return grpcClientConfig.createChannel("payment");
 *  }
 *  @Bean
 *  WalletPointServiceBlockingStub walletPointServiceBlockingStub(ManagedChannel paymentManagedChannel) {
 *      return WalletPointServiceGrpc.newBlockingStub(paymentManagedChannel);
 *  }
 */
@Configuration
public class DonationGrpcClientConfig {
    // TODO(gRPC): payment 에 포인트 차감 RPC 추가되면 채널 + 차감 stub 빈 등록
}