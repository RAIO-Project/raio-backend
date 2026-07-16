package raio.user.grpc.client.config;

import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.payment.grpc.WalletCommandServiceGrpc;
import raio.payment.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

@Configuration
public class UserPaymentGrpcClientConfig {

    private static final String SERVICE_NAME = "payment";

    @Bean(destroyMethod = "shutdown")
    ManagedChannel paymentManagedChannel(GrpcClientConfig grpcClientConfig) {
        return grpcClientConfig.createChannel(SERVICE_NAME);
    }

    /** 빈 이름 = walletStub (UserGrpcClientAdapter 의 필드명과 일치) */
    @Bean
    WalletCommandServiceBlockingStub walletStub(ManagedChannel paymentManagedChannel) {
        return WalletCommandServiceGrpc.newBlockingStub(paymentManagedChannel);
    }
}