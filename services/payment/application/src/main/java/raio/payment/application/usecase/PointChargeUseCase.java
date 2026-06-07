package raio.payment.application.usecase;

import raio.payment.domain.Wallet;

public interface PointChargeUseCase {
    
    Wallet charge(String walletId, Long amount);
}
