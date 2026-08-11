package raio.wallet.application.usecase;

import raio.wallet.domain.Wallet;

public interface WalletCreateUseCase {
    
    Wallet create(String userId);
}
