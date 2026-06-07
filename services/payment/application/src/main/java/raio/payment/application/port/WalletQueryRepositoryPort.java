package raio.payment.application.port;

import raio.payment.domain.Wallet;

import java.util.Optional;

public interface WalletQueryRepositoryPort {
    
    Optional<Wallet> findById(String id);
    
    Optional<Wallet> findByUserId(String userId);
}
