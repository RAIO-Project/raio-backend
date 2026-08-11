package raio.wallet.application.usecase;

import raio.wallet.domain.Wallet;

public interface PointDonateUseCase {
    
    Wallet donate(String walletId, Long amount);
}
