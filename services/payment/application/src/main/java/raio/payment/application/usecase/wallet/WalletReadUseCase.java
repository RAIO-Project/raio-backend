package raio.payment.application.usecase.wallet;

import raio.payment.domain.wallet.Wallet;

public interface WalletReadUseCase {
    
    Wallet getWallet(String userId);
}
