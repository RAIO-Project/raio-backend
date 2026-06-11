package raio.payment.application.usecase;

import raio.payment.domain.Wallet;

public interface PointDonateUseCase {
    
    Wallet donate(String walletId, Long amount);
}
