package raio.wallet.application.usecase;

import raio.wallet.domain.Wallet;

public interface WalletReadUseCase {
    
    Wallet getWallet(String userId);
}
