package raio.user.grpc.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raio.payment.grpc.CreateWalletRequest;
import raio.payment.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;
import raio.user.application.port.PaymentCommandPort;

@Component
@RequiredArgsConstructor
public class UserGrpcClientAdapter implements PaymentCommandPort {
    
    private final WalletCommandServiceBlockingStub walletStub;
    
    @Override
    public void createWallet(String userId) {
        walletStub.createWallet(
                CreateWalletRequest.newBuilder()
                        .setUserId(userId)
                        .build()
        );
    }
}