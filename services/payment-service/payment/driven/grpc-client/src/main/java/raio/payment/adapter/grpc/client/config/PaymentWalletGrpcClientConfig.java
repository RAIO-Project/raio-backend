package raio.payment.adapter.grpc.client.config;

import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.wallet.grpc.WalletCommandServiceGrpc;
import raio.wallet.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

@Configuration
public class PaymentWalletGrpcClientConfig {
    
    @Bean
    ManagedChannel paymentWalletChannel(GrpcClientConfig config) {
        return config.createChannel("wallet");
    }
    
    @Bean
    WalletCommandServiceBlockingStub paymentWalletCommandServiceBlockingStub(
            @Qualifier("paymentWalletChannel")
            ManagedChannel managedChannel
    ) {
        return WalletCommandServiceGrpc.newBlockingStub(managedChannel);
    }
}
