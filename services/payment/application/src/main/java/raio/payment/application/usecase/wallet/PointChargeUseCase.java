package raio.payment.application.usecase.wallet;

import raio.payment.domain.wallet.Wallet;

public interface PointChargeUseCase {
    
    Wallet charge(String walletId, Long amount);
}
