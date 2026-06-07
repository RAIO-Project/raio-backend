package raio.payment.application.port;

import raio.payment.domain.Wallet;

import java.util.Optional;

public interface WalletCommandRepositoryPort {
    
    boolean existsById(String id);
    
    Wallet save(Wallet wallet);
    
    Optional<Wallet> increaseBalance(String walletId, Long amount);
    
    Optional<Wallet> decreaseBalance(String walletId, Long amount);
}
