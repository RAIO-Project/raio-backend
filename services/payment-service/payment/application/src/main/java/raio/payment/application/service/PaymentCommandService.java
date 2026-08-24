package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.application.port.PaymentClientPort;
import raio.payment.application.port.PaymentCommandRepositoryPort;
import raio.payment.application.port.WalletCommandPort;
import raio.payment.application.usecase.PaymentConfirmUseCase;
import raio.payment.application.usecase.PaymentPrepareUseCase;
import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;

import java.util.UUID;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_ALREADY_PROCESSED;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_CONFIRM_FAILED;
import static raio.payment.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandService implements PaymentPrepareUseCase, PaymentConfirmUseCase {
    
    private final PaymentCommandRepositoryPort paymentCommandRepositoryPort;
    private final WalletCommandPort walletCommandPort;
    private final PaymentClientPort paymentClientPort;
    
    @Override
    public Payment prepare(PrepareCommand command) {
        var payment = Payment.builder()
                .orderId(UUID.randomUUID().toString().replace("-", ""))
                .userId(command.userId())
                .amount(command.amount())
                .status(PaymentStatus.READY)
                .method(command.method())
                .pgProvider(command.pgProvider())
                .build();
        
        var prepared  = paymentCommandRepositoryPort.save(payment);
        
        log.debug(
                "[결제 준비(PREPARE)] paymentId={}, orderId={}, userId={}, amount={}, method={}, pgProvider={}, status={}",
                prepared.getId(),
                prepared.getOrderId(),
                prepared.getUserId(),
                prepared.getAmount(),
                prepared.getMethod(),
                prepared.getPgProvider(),
                prepared.getStatus()
        );
        
        return prepared;
    }
    
    /**
     * 결제 확정 흐름 (트랜잭션 경계 분리):
     * [Tx 1] 비관적 락 → 검증 → APPROVING
     * 외부 PG 호출 (트랜잭션 없음)
     * [Tx 2] APPROVED/FAILED 확정
     * APPROVED 시 지갑 충전 요청
     */
    @Override
    public Payment confirm(ConfirmCommand command) {
        
        log.debug(
                "[결제 승인 요청(CONFIRM)] paymentId={}, orderId={}, amount={}, externalKey={}",
                command.paymentId(),
                command.orderId(),
                command.amount(),
                command.externalKey()
        );
        
        // [Tx 1] 비관적 락 → 검증 → APPROVING
        // PESSIMISTIC_WRITE로 결제를 잠근 뒤 검증하고 APPROVING 상태로 전이한다.
        var approving = paymentCommandRepositoryPort.transaction(() -> {
            
            var payment = paymentCommandRepositoryPort.findByIdForUpdate(command.paymentId())
                    .orElseThrow(PAYMENT_NOT_FOUND::exception);
            
            log.debug(
                    "[결제 조회 및 잠금(LOCK)] paymentId={}, orderId={}, status={}, amount={}",
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getStatus(),
                    payment.getAmount()
            );
            
            if (payment.getStatus() != PaymentStatus.READY) {
                log.warn(
                        "[이미 처리된 결제(ALREADY_PROCESSED)] paymentId={}, status={}",
                        payment.getId(),
                        payment.getStatus()
                );
                
                throw PAYMENT_ALREADY_PROCESSED.exception();
            }
            
            if (!payment.getAmount().equals(command.amount())) {
                log.warn(
                        "[결제 금액 불일치(AMOUNT_MISMATCH)] paymentId={}, paymentAmount={}, requestAmount={}",
                        payment.getId(),
                        payment.getAmount(),
                        command.amount()
                );
                
                throw PAYMENT_AMOUNT_MISMATCH.exception();
            }
            
            if (!payment.getOrderId().equals(command.orderId())) {
                log.warn(
                        "[주문번호 불일치(ORDER_ID_MISMATCH)] paymentId={}, paymentOrderId={}, requestOrderId={}",
                        payment.getId(),
                        payment.getOrderId(),
                        command.orderId()
                );
                
                throw PAYMENT_AMOUNT_MISMATCH.exception();
            }
            
            var updated = paymentCommandRepositoryPort
                    .updateStatus(
                            command.paymentId(),
                            PaymentStatus.APPROVING,
                            command.externalKey(),
                            null
                    )
                    .orElseThrow(PAYMENT_NOT_FOUND::exception);
            
            log.debug(
                    "[결제 승인 진행(APPROVING)] paymentId={}, orderId={}, status={}",
                    updated.getId(),
                    updated.getOrderId(),
                    updated.getStatus()
            );
            
            // 트랜잭션 종료 시 락이 해제되므로 외부 PG 호출 중 DB 락이 유지되지 않는다.
            return updated;
        });
        
        // PG사 승인 검증 호출
        log.debug(
                "[PG 승인 요청(PG_CONFIRM)] paymentId={}, orderId={}, amount={}",
                approving.getId(),
                command.orderId(),
                command.amount()
        );
        
        var result = paymentClientPort.confirm(
                command.externalKey(),
                command.orderId(),
                command.amount()
        );
        
        if (!result.success()) {
            log.error(
                    "[결제 승인 실패(FAILED)] paymentId={}, orderId={}, failMessage={}",
                    approving.getId(),
                    approving.getOrderId(),
                    result.failMessage()
            );
            
            paymentCommandRepositoryPort.transaction(() ->
                    paymentCommandRepositoryPort
                            .updateStatus(
                                    approving.getId(),
                                    PaymentStatus.FAILED,
                                    null,
                                    result.failMessage()
                            )
                            .orElseThrow(PAYMENT_NOT_FOUND::exception)
            );
            
            throw PAYMENT_CONFIRM_FAILED.exception();
        }
        
        // [Tx 2] APPROVED 확정
        var approvedPayment  = paymentCommandRepositoryPort.transaction(() -> {
            var approved = paymentCommandRepositoryPort
                    .updateStatus(
                            approving.getId(),
                            PaymentStatus.APPROVED,
                            result.externalTid(),
                            null
                    )
                    .orElseThrow(PAYMENT_NOT_FOUND::exception);
            
            log.info(
                    "[결제 승인 완료(APPROVED)] paymentId={}, orderId={}, userId={}, amount={}, externalTid={}",
                    approved.getId(),
                    approved.getOrderId(),
                    approved.getUserId(),
                    approved.getAmount(),
                    result.externalTid()
            );
            
            return approved;
        });
        
        // 지갑 충전 요청 (트랜잭션 밖 — Toss 호출과 동일한 패턴)
        if(approvedPayment.getStatus() != PaymentStatus.APPROVED) {
            throw PAYMENT_CONFIRM_FAILED.exception();
        }

        log.debug(
                "[포인트 충전 요청(POINT_CHARGE)] paymentId={}, walletId={}, amount={}",
                approvedPayment.getId(),
                approvedPayment.getId(),
                approvedPayment.getAmount()
        );

        // 지갑 포인트 충전
        walletCommandPort.increaseWalletBalance(approvedPayment.getUserId(), approvedPayment.getId(), approvedPayment.getAmount());

        log.info(
                "[포인트 충전 완료(POINT_CHARGED)] paymentId={}, walletId={}, amount={}",
                approvedPayment.getId(),
                approvedPayment.getId(),
                approvedPayment.getAmount()
        );

        return approvedPayment;
    }
}
