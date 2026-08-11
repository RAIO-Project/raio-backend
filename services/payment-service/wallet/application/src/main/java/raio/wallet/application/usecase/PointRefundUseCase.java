package raio.wallet.application.usecase;

import raio.wallet.domain.Wallet;

public interface PointRefundUseCase {
    
    Wallet refund(String walletId, Long balance);
}
