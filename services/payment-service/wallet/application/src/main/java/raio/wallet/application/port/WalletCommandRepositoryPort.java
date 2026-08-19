package raio.wallet.application.port;

import raio.wallet.domain.Wallet;

import java.util.Optional;

public interface WalletCommandRepositoryPort {
    
    boolean existsById(String id);
    
    Optional<Wallet> findByUserId(String userId);
    
    Wallet save(Wallet wallet);
    
    Optional<Wallet> increaseBalance(String walletId, Long amount);
    
    Optional<Wallet> decreaseBalance(String walletId, Long amount);
}
