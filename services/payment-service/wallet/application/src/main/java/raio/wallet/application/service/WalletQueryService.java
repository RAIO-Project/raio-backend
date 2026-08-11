package raio.wallet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raio.wallet.application.port.WalletQueryRepositoryPort;
import raio.wallet.application.usecase.WalletReadUseCase;
import raio.wallet.domain.Wallet;

import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalletQueryService implements WalletReadUseCase {
    
    private final WalletQueryRepositoryPort walletQueryRepositoryPort;
    
    @Override
    public Wallet getWallet(String userId) {
        return walletQueryRepositoryPort.findByUserId(userId).orElseThrow(WALLET_NOT_FOUND::exception);
    }
}
