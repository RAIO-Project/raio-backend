package raio.payment.application.usecase;

import raio.payment.domain.Wallet;

public interface PointRefundUseCase {
    
    Wallet refund(String walletId, Long balance);
}
