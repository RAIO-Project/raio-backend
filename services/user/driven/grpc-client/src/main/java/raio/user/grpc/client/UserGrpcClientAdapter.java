package raio.user.grpc.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import raio.user.application.port.PaymentCommandPort;
import raio.wallet.grpc.CreateWalletRequest;
import raio.wallet.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

@Component
public class UserGrpcClientAdapter implements PaymentCommandPort {
    
    private final WalletCommandServiceBlockingStub walletStub;
    
    public UserGrpcClientAdapter(
            @Qualifier("userWalletCommandServiceBlockingStub")
            WalletCommandServiceBlockingStub walletStub
    ) {
        this.walletStub = walletStub;
    }
    
    @Override
    public void createWallet(String userId) {
        walletStub.createWallet(
                CreateWalletRequest.newBuilder()
                        .setUserId(userId)
                        .build()
        );
    }
}