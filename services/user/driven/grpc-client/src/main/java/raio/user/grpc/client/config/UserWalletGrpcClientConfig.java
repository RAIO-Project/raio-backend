package raio.user.grpc.client.config;

import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.wallet.grpc.WalletCommandServiceGrpc;
import raio.wallet.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

@Configuration
public class UserWalletGrpcClientConfig {
    
    @Bean(destroyMethod = "shutdown")
    ManagedChannel userWalletManagedChannel(GrpcClientConfig grpcClientConfig) {
        return grpcClientConfig.createChannel("wallet");
    }
    
    @Bean
    WalletCommandServiceBlockingStub userWalletCommandServiceBlockingStub(
            @Qualifier("userWalletManagedChannel")
            ManagedChannel managedChannel
    ) {
        return WalletCommandServiceGrpc.newBlockingStub(managedChannel);
    }
}