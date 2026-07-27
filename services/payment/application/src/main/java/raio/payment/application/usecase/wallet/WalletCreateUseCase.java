package raio.payment.application.usecase.wallet;

import raio.payment.domain.wallet.Wallet;

public interface WalletCreateUseCase {
    Wallet create(String userId);
}
