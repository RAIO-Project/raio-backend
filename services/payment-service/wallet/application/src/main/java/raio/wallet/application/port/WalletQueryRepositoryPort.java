package raio.wallet.application.port;

import raio.wallet.domain.Wallet;

import java.util.Optional;

public interface WalletQueryRepositoryPort {
    
    Optional<Wallet> findById(String id);
    
    Optional<Wallet> findByUserId(String userId);
}
