package raio.payment.application.port;

import raio.wallet.domain.Wallet;

import java.util.Optional;

public interface WalletCommandPort {
   
   Optional<Wallet> findWalletByUserId (String userId);
   
   void increaseWalletBalance (String userId, String sourceId, Long amount);
}
