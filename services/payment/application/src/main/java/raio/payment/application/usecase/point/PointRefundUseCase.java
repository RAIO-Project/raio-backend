package raio.payment.application.usecase.point;

import raio.payment.domain.wallet.Wallet;

public interface PointRefundUseCase {
    
    Wallet refund(String walletId, Long balance);
}
