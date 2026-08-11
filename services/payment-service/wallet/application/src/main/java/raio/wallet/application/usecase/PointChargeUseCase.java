package raio.wallet.application.usecase;

import raio.wallet.domain.Wallet;

public interface PointChargeUseCase {
    
    Wallet charge(String walletId, Long amount);
}
