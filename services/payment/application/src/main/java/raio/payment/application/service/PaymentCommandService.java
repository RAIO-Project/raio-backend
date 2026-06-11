package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.application.port.PaymentClientPort;
import raio.payment.application.port.PaymentCommandRepositoryPort;
import raio.payment.application.usecase.PaymentConfirmUseCase;
import raio.payment.application.usecase.PaymentPrepareUseCase;
import raio.payment.application.usecase.PointChargeUseCase;
import raio.payment.application.usecase.WalletReadUseCase;
import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;

import java.util.UUID;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_ALREADY_PROCESSED;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_CONFIRM_FAILED;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentPrepareUseCase, PaymentConfirmUseCase {
    
    private final WalletReadUseCase walletReadUseCase;
    private final PointChargeUseCase pointChargeUseCase;
    private final PaymentCommandRepositoryPort paymentCommandRepositoryPort;
    private final PaymentClientPort paymentClientPort;
    
    @Override
    public Payment prepare(PrepareCommand command) {
        var payment = Payment.builder().orderId(UUID.randomUUID().toString().replace("-", "")).userId(command.userId()).amount(command.amount()).status(PaymentStatus.READY).method(command.method()).pgProvider(command.pgProvider()).build();
        
        return paymentCommandRepositoryPort.save(payment);
    }
    
    /**
     * 결제 확정 흐름 (트랜잭션 경계 분리):
     * [Tx 1] 비관적 락 → 검증 → APPROVING
     * 외부 PG 호출 (트랜잭션 없음)
     * [Tx 2] APPROVED/FAILED 확정 + 지갑 충전
     */
    @Override
    public Payment confirm(ConfirmCommand command) {
        // [Tx 1] 비관적 락 → 검증 → APPROVING
        // PESSIMISTIC_WRITE로 결제를 잠근 뒤 검증하고 APPROVING 상태로 전이한다.
        var approving = paymentCommandRepositoryPort.transaction(() -> {
            var payment = paymentCommandRepositoryPort.findByIdForUpdate(command.paymentId()).orElseThrow(PAYMENT_NOT_FOUND::exception);
            
            if (payment.getStatus() != PaymentStatus.READY) {
                throw PAYMENT_ALREADY_PROCESSED.exception();
            }
            if (!payment.getAmount().equals(command.amount())) {
                throw PAYMENT_AMOUNT_MISMATCH.exception();
            }
            if (!payment.getOrderId().equals(command.orderId())) {
                throw PAYMENT_AMOUNT_MISMATCH.exception();
            }
            
            // 트랜잭션 종료 시 락이 해제되므로 외부 PG 호출 중 DB 락이 유지되지 않는다.
            return paymentCommandRepositoryPort.updateStatus(command.paymentId(), PaymentStatus.APPROVING, command.externalKey(), null).orElseThrow(PAYMENT_NOT_FOUND::exception);
        });
        
        // PG사 승인 검증 호출
        var result = paymentClientPort.confirm(command.externalKey(), command.orderId(), command.amount());
        
        // [Tx 2] APPROVED/FAILED 확정 + 지갑 충전
        // PG 승인 결과에 따라 결제 상태를 확정하고, 성공 시 지갑을 충전한다.
        if (result.success()) {
            
            return paymentCommandRepositoryPort.transaction(() -> {
                Payment done = paymentCommandRepositoryPort.updateStatus(approving.getId(), PaymentStatus.APPROVED, result.externalTid(), null).orElseThrow(PAYMENT_NOT_FOUND::exception);
                
                // 사용자 지갑 조회
                var wallet = walletReadUseCase.getWallet(done.getUserId());
                
                // 지갑 포인트 충전
                pointChargeUseCase.charge(wallet.getId(), done.getAmount() + command.amount());
                
                return paymentCommandRepositoryPort.updateStatus(done.getId(), PaymentStatus.FAILED, null, result.failMessage()).orElseThrow(PAYMENT_NOT_FOUND::exception);
            });
        } else {
            throw PAYMENT_CONFIRM_FAILED.exception();
        }
    }
}
