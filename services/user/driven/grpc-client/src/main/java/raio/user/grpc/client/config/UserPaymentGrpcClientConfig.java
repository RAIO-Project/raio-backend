package raio.user.grpc.client.config;

import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.payment.grpc.WalletCommandServiceGrpc;
import raio.payment.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

@Configuration
public class PaymentGrpcClientConfig {
    
    @Bean(destroyMethod = "shutdown")
    ManagedChannel paymentManagedChannel(GrpcClientConfig grpcClientConfig) {
        return grpcClientConfig.createChannel("payment");
    }
    
    @Bean
    WalletCommandServiceBlockingStub walletCommandServiceBlockingStub(
            ManagedChannel paymentManagedChannel
    ) {
        return WalletCommandServiceGrpc.newBlockingStub(paymentManagedChannel);
    }
}