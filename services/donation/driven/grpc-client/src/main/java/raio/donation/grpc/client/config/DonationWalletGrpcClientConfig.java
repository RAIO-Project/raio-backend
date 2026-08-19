package raio.donation.grpc.client.config;

import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.config.GrpcClientConfig;
import raio.wallet.grpc.WalletCommandServiceGrpc;

@Configuration
public class DonationWalletGrpcClientConfig {

    private static final String SERVICE_NAME = "wallet";

    @Bean(destroyMethod = "shutdown")
    ManagedChannel donationWalletManagedChannel(GrpcClientConfig grpcClientConfig) {
        return grpcClientConfig.createChannel(SERVICE_NAME);
    }

    /** 빈 이름 = donationWalletStub (DonationGrpcClientAdapter 의 필드명과 일치) */
    @Bean
    WalletCommandServiceGrpc.WalletCommandServiceBlockingStub donationWalletStub(
            @Qualifier("donationWalletManagedChannel") ManagedChannel donationWalletManagedChannel
    ) {
        return WalletCommandServiceGrpc.newBlockingStub(donationWalletManagedChannel);
    }
}
