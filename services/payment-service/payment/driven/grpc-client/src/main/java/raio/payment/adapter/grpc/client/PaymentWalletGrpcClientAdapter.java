package raio.payment.adapter.grpc.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import raio.payment.application.port.WalletCommandPort;
import raio.wallet.domain.Wallet;
import raio.wallet.grpc.ChargePointsRequest;
import raio.wallet.grpc.GetWalletRequest;
import raio.wallet.grpc.WalletCommandServiceGrpc.WalletCommandServiceBlockingStub;

import java.util.Optional;

import static raio.payment.exception.PaymentErrorCode.WALLET_NOT_FOUND;
import static raio.payment.exception.PaymentErrorCode.WALLET_SERVICE_TIMEOUT;
import static raio.payment.exception.PaymentErrorCode.WALLET_SERVICE_UNAVAILABLE;

@Component
public class PaymentWalletGrpcClientAdapter implements WalletCommandPort {
    
    private final WalletCommandServiceBlockingStub walletCommandServiceBlockingStub;
    
    public PaymentWalletGrpcClientAdapter(
            @Qualifier("paymentWalletCommandServiceBlockingStub")
            WalletCommandServiceBlockingStub walletCommandServiceBlockingStub) {
        this.walletCommandServiceBlockingStub = walletCommandServiceBlockingStub;
    }
    
    @Override
    public Optional<Wallet> findWalletByUserId(String userId) {
        var request = GetWalletRequest.newBuilder()
                .setUserId(userId)
                .build();
        
        try {
            var response = walletCommandServiceBlockingStub.getWallet(request);
            
            return Optional.of(
                    Wallet.builder()
                            .id(response.getWalletId())
                            .userId(response.getUserId())
                            .balance(response.getBalance())
                            .build()
            );
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return Optional.empty();
            }
            
            throw e;
        }
    }
    
    @Override
    public void increaseWalletBalance(String userId, String sourceId, Long amount) {
        var request = ChargePointsRequest.newBuilder()
                .setUserId(userId)
                .setSourceId(sourceId)
                .setAmount(amount)
                .build();
        
        try {
            walletCommandServiceBlockingStub.chargePoints(request);
            
        } catch (StatusRuntimeException e) {
            throw switch (e.getStatus().getCode()) {
                case NOT_FOUND -> WALLET_NOT_FOUND.exception(e);
                
                case UNAVAILABLE -> WALLET_SERVICE_UNAVAILABLE.exception(e);
                
                case DEADLINE_EXCEEDED -> WALLET_SERVICE_TIMEOUT.exception(e);
                
                default -> throw e;
            };
        }
    }
}
