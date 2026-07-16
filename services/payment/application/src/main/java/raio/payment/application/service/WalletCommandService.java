package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.payment.application.port.PointHistoryCommandRepositoryPort;
import raio.payment.application.port.WalletCommandRepositoryPort;
import raio.payment.application.usecase.PointChargeUseCase;
import raio.payment.application.usecase.PointDonateUseCase;
import raio.payment.application.usecase.PointRefundUseCase;
import raio.payment.application.usecase.WalletCreateUseCase;
import raio.payment.domain.PointHistory;
import raio.payment.domain.Wallet;
import raio.payment.domain.type.PointHistoryType;
import raio.payment.exception.PaymentException;

import static raio.payment.exception.PaymentErrorCode.INSUFFICIENT_POINT_BALANCE;
import static raio.payment.exception.PaymentErrorCode.INVALID_POINT_AMOUNT;
import static raio.payment.exception.PaymentErrorCode.USER_NOT_FOUND;
import static raio.payment.exception.PaymentErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WalletCommandService implements WalletCreateUseCase, PointChargeUseCase, PointRefundUseCase, PointDonateUseCase {
    
    private final WalletCommandRepositoryPort walletCommandRepositoryPort;
    private final PointHistoryCommandRepositoryPort pointHistoryCommandRepositoryPort;
    
    @Override
    public Wallet create(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new PaymentException(USER_NOT_FOUND);
        }
        
        var newWallet = Wallet.builder().userId(userId).balance(0L).build();
        
        return walletCommandRepositoryPort.save(newWallet);
    }
    
    @Override
    @Transactional
    public Wallet charge(String walletId, Long amount) {
        validateAmount(amount);
        
        var chargedWallet = walletCommandRepositoryPort.increaseBalance(walletId, amount).orElseThrow(WALLET_NOT_FOUND::exception);
        
        savePointHistory(chargedWallet, PointHistoryType.CHARGE, amount);
        
        return chargedWallet;
    }
    
    @Override
    @Transactional
    public Wallet refund(String walletId, Long amount) {
        validateAmount(amount);
        
        var refundedWallet = walletCommandRepositoryPort.decreaseBalance(walletId, amount).orElseThrow(INSUFFICIENT_POINT_BALANCE::exception);
        
        savePointHistory(refundedWallet, PointHistoryType.REFUND, amount);
        
        return refundedWallet;
    }
    
    @Override
    @Transactional
    public Wallet donate(String userId, Long amount) {
        validateAmount(amount);
        
        // 포인트 차감 대상이 되는 사용자의 지갑
        var wallet = walletCommandRepositoryPort.findByUserId(userId)
                .orElseThrow(WALLET_NOT_FOUND::exception);
                
        var donatedWallet = walletCommandRepositoryPort.decreaseBalance(wallet.getId(), amount).orElseThrow(INSUFFICIENT_POINT_BALANCE::exception);
        
        savePointHistory(donatedWallet, PointHistoryType.DONATION, amount);
        
        return donatedWallet;
    }
    
    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new PaymentException(INVALID_POINT_AMOUNT);
        }
    }
    
    private void savePointHistory(Wallet wallet, PointHistoryType type, Long amount) {
        var pointHistory = PointHistory.builder().walletId(wallet.getId()).userId(wallet.getUserId()).type(type).amount(amount).balanceSnapshot(wallet.getBalance()).build();
        
        pointHistoryCommandRepositoryPort.save(pointHistory);
    }
}