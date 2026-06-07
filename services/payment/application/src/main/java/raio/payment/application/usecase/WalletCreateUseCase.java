package raio.payment.application.usecase;

import raio.payment.domain.Wallet;

public interface WalletCreateUseCase {
    Wallet create(String userId);
}
