package raio.wallet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.wallet.application.port.PointHistoryCommandRepositoryPort;
import raio.wallet.application.port.SettlementCommandPort;
import raio.wallet.application.port.WalletCommandRepositoryPort;
import raio.wallet.application.port.WalletQueryRepositoryPort;
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
import static raio.wallet.exception.WalletErrorCode.INVALID_SOURCE_ID;
import static raio.wallet.exception.WalletErrorCode.USER_NOT_FOUND;
import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletCommandService implements WalletCreateUseCase, PointChargeUseCase, PointRefundUseCase, PointDonateUseCase {

    private final WalletCommandRepositoryPort walletCommandRepositoryPort;
    private final WalletQueryRepositoryPort walletQueryRepositoryPort;
    private final PointHistoryCommandRepositoryPort pointHistoryCommandRepositoryPort;
    private final SettlementCommandPort settlementCommandPort;
    
    @Override
    public Wallet create(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new WalletException(USER_NOT_FOUND);
        }
        
        // 지갑 생성
        var newWallet = Wallet.builder().userId(userId).balance(0L).build();

        // 사용자 기본 정산 세팅 생성 (정산 서비스 장애로 실패하더라도 지갑 생성 자체는 계속 진행한다)
        try {
            settlementCommandPort.save(userId);
        } catch (Exception e) {
            log.error("[정산 설정 저장 실패(SETTLEMENT_SETTING_SAVE_FAILED)] userId={}", userId, e);
        }
        
        return walletCommandRepositoryPort.save(newWallet);
    }
    
    @Override
    @Transactional
    public Wallet charge(String walletId, String sourceId, Long amount) {
        validateAmount(amount);
        validateSourceId(sourceId);

        var existing = pointHistoryCommandRepositoryPort.findByWalletIdAndTypeAndSourceId(walletId, PointHistoryType.CHARGE, sourceId);

        if (existing.isPresent()) {
            log.info("[중복 충전 요청(IDEMPOTENT_SKIP)] walletId={}, sourceId={}", walletId, sourceId);

            return walletQueryRepositoryPort.findById(walletId).orElseThrow(WALLET_NOT_FOUND::exception);
        }

        var chargedWallet = walletCommandRepositoryPort.increaseBalance(walletId, amount).orElseThrow(WALLET_NOT_FOUND::exception);

        savePointHistory(chargedWallet, PointHistoryType.CHARGE, amount, sourceId);

        return chargedWallet;
    }

    @Override
    @Transactional
    public Wallet refund(String walletId, String sourceId, Long amount) {
        validateAmount(amount);
        validateSourceId(sourceId);

        var existing = pointHistoryCommandRepositoryPort.findByWalletIdAndTypeAndSourceId(walletId, PointHistoryType.REFUND, sourceId);

        if (existing.isPresent()) {
            log.info("[중복 환불 요청(IDEMPOTENT_SKIP)] walletId={}, sourceId={}", walletId, sourceId);

            return walletQueryRepositoryPort.findById(walletId).orElseThrow(WALLET_NOT_FOUND::exception);
        }

        var refundedWallet = walletCommandRepositoryPort.decreaseBalance(walletId, amount).orElseThrow(INSUFFICIENT_POINT_BALANCE::exception);

        savePointHistory(refundedWallet, PointHistoryType.REFUND, amount, sourceId);

        return refundedWallet;
    }

    @Override
    @Transactional
    public Wallet donate(String walletId, Long amount, String sourceId) {
        validateAmount(amount);
        
        var existing = pointHistoryCommandRepositoryPort.findByWalletIdAndTypeAndSourceId(walletId, PointHistoryType.DONATION, sourceId);
        
        if (existing.isPresent()) {
            log.info("[중복 후원 요청(IDEMPOTENT_SKIP)] walletId={}, sourceId={}", walletId, sourceId);
            
            return walletQueryRepositoryPort.findById(walletId).orElseThrow(WALLET_NOT_FOUND::exception);
        }
        
        var donatedWallet = walletCommandRepositoryPort.decreaseBalance(walletId, amount).orElseThrow(INSUFFICIENT_POINT_BALANCE::exception);

        savePointHistory(donatedWallet, PointHistoryType.DONATION, amount, null);

        return donatedWallet;
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new WalletException(INVALID_POINT_AMOUNT);
        }
    }

    private void validateSourceId(String sourceId) {
        if (sourceId == null || sourceId.isBlank()) {
            throw new WalletException(INVALID_SOURCE_ID);
        }
    }

    private void savePointHistory(Wallet wallet, PointHistoryType type, Long amount, String sourceId) {
        var pointHistory = PointHistory.builder().walletId(wallet.getId()).userId(wallet.getUserId()).type(type).amount(amount).balanceSnapshot(wallet.getBalance()).sourceId(sourceId).build();

        pointHistoryCommandRepositoryPort.save(pointHistory);
    }
}