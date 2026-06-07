package raio.payment.application.usecase;

import raio.payment.domain.Wallet;

public interface WalletReadUseCase {
    
    Wallet getWallet(String userId);
}
