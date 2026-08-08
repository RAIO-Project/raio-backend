package raio.payment.application.usecase.wallet;

import raio.payment.domain.wallet.Wallet;

public interface PointDonateUseCase {
    
    Wallet donate(String walletId, Long amount);
}
