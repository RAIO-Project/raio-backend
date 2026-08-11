package raio.wallet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.wallet.application.port.PointHistoryCommandRepositoryPort;
import raio.wallet.application.port.WalletCommandRepositoryPort;
import raio.wallet.application.usecase.PointChargeUseCase;
import raio.wallet.application.usecase.PointDonateUseCase;
import raio.wallet.application.usecase.PointRefundUseCase;
import raio.wallet.application.usecase.WalletCreateUseCase;
import raio.wallet.domain.PointHistory;
import raio.wallet.domain.Wallet;
import raio.wallet.domain.type.PointHistoryType;
import raio.wallet.exception.WalletException;

import static raio.wallet.exception.WalletErrorCode.INSUFFICIENT_POINT_BALANCE;
import static raio.wallet.exception.WalletErrorCode.INVALID_POINT_AMOUNT;
import static raio.wallet.exception.WalletErrorCode.USER_NOT_FOUND;
import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class WalletCommandService implements WalletCreateUseCase, PointChargeUseCase, PointRefundUseCase, PointDonateUseCase {
    
    private final WalletCommandRepositoryPort walletCommandRepositoryPort;
    private final PointHistoryCommandRepositoryPort pointHistoryCommandRepositoryPort;
    
    @Override
    public Wallet create(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new WalletException(USER_NOT_FOUND);
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
    public Wallet donate(String walletId, Long amount) {
        validateAmount(amount);
        
        var donatedWallet = walletCommandRepositoryPort.decreaseBalance(walletId, amount).orElseThrow(INSUFFICIENT_POINT_BALANCE::exception);
        
        savePointHistory(donatedWallet, PointHistoryType.DONATION, amount);
        
        return donatedWallet;
    }
    
    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new WalletException(INVALID_POINT_AMOUNT);
        }
    }
    
    private void savePointHistory(Wallet wallet, PointHistoryType type, Long amount) {
        var pointHistory = PointHistory.builder().walletId(wallet.getId()).userId(wallet.getUserId()).type(type).amount(amount).balanceSnapshot(wallet.getBalance()).build();
        
        pointHistoryCommandRepositoryPort.save(pointHistory);
    }
}