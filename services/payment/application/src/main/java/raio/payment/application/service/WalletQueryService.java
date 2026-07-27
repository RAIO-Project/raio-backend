package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raio.payment.application.port.WalletQueryRepositoryPort;
import raio.payment.application.usecase.wallet.WalletReadUseCase;
import raio.payment.domain.wallet.Wallet;

import static raio.payment.exception.PaymentErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalletQueryService implements WalletReadUseCase {
    
    private final WalletQueryRepositoryPort walletQueryRepositoryPort;
    
    @Override
    public Wallet getWallet(String userId) {
        return walletQueryRepositoryPort.findByUserId(userId).orElseThrow(WALLET_NOT_FOUND::exception);
    }
}
