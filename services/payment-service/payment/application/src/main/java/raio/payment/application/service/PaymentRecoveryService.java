package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.payment.application.port.PaymentClientPort;
import raio.payment.application.port.PaymentCommandRepositoryPort;
import raio.payment.application.port.WalletCommandPort;
import raio.payment.application.usecase.PaymentRecoveryUseCase;
import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;

import java.util.List;

import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRecoveryService implements PaymentRecoveryUseCase {
    
    private final PaymentCommandRepositoryPort paymentCommandRepositoryPort;
    private final WalletCommandPort walletCommandPort;
    private final PaymentClientPort paymentClientPort;

    @Override
    public void recover() {
        List<Payment> stuckPayments = paymentCommandRepositoryPort.findApprovingWithPaymentKey();
        log.info("[결제 복구] APPROVING 결제 {}건 발견", stuckPayments.size());

        for (Payment payment : stuckPayments) {
            recoverOne(payment);
        }
    }

    private void recoverOne(Payment payment) {
        try {
            var result = paymentClientPort.confirm(
                    payment.getExternalTid(),
                    payment.getOrderId(),
                    payment.getAmount()
            );

            if (result.success()) {
                paymentCommandRepositoryPort.transaction(() -> {
                    paymentCommandRepositoryPort.updateStatus(payment.getId(), PaymentStatus.APPROVED, result.externalTid(), null);
                    
                    var wallet = walletCommandPort.findWalletByUserId(payment.getUserId()).orElseThrow(WALLET_NOT_FOUND::exception);
                    
                    walletCommandPort.increaseWalletBalance(wallet.getId(), payment.getAmount());
                    
                    return null;
                });
                
                log.info("[결제 복구] 승인 완료 paymentId={}", payment.getId());
            
            } else {
                paymentCommandRepositoryPort.transaction(() ->
                        paymentCommandRepositoryPort.updateStatus(payment.getId(), PaymentStatus.FAILED, null, result.failMessage())
                );
            
                log.warn("[결제 복구] PG 승인 실패 paymentId={}, reason={}", payment.getId(), result.failMessage());
            }

        } catch (Exception e) {
            log.error("[결제 복구] 예외 발생 paymentId={}", payment.getId(), e);
            
            paymentCommandRepositoryPort.transaction(() ->
                    paymentCommandRepositoryPort.updateStatus(payment.getId(), PaymentStatus.FAILED, null, e.getMessage())
            );
        }
    }
}
