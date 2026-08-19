package raio.wallet.adapter.grpc.client.config;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.settlement.grpc.SettlementCommandServiceGrpc;
import raio.settlement.grpc.SettlementCommandServiceGrpc.SettlementCommandServiceBlockingStub;

@Configuration
@RequiredArgsConstructor
public class WalletSettlementGrpcClientConfig {
    
    private final GrpcClientConfig grpcClientConfig;
    
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel walletSettlementManagedChannel(GrpcClientConfig grpcClientConfig) {
        return grpcClientConfig.createChannel("settlement");
    }
    
    @Bean
    SettlementCommandServiceBlockingStub walletSettlementCommandServiceBlockingStub(
            @Qualifier("walletSettlementManagedChannel")
            ManagedChannel managedChannel
    ) {
        return SettlementCommandServiceGrpc.newBlockingStub(managedChannel);
    }
}
