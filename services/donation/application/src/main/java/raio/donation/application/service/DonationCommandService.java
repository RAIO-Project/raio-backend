package raio.donation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.donation.application.port.DonationCommandPort;
import raio.donation.application.port.PaymentCommandPort;
import raio.donation.application.usecase.DonationCreateUseCase;
import raio.donation.application.usecase.DonationNotifyUseCase;
import raio.donation.domain.Donations;
import raio.donation.domain.type.DonationStatus;

import static raio.donation.exception.DonationErrorCode.PAYMENT_FAILED;

/**
 * 후원 생성 플로우.
 * 1) 포인트 차감 ({@link PaymentCommandPort}) — 실패 시 후원 중단
 * 2) 후원 영속화 ({@link DonationCommandPort}) — id 채번
 * 3) 시청자 실시간 알림 — {@link DonationNotifyUseCase} 에 위임
 *
 * <p>차감은 외부 서비스(payment)에서 확정되므로 이 서비스의 트랜잭션에 묶이지 않는다.
 * 저장이 실패하면 포인트만 빠진 상태가 되므로 보상(환불)이 필요하다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationCommandService implements DonationCreateUseCase {

    private final PaymentCommandPort paymentCommandPort;
    private final DonationCommandPort donationCommandPort;
    private final DonationNotifyUseCase donationNotifyUseCase;

    @Override
    public Long create(DonationCreateCommand command) {
        // 1) 포인트 차감
        boolean paid = paymentCommandPort.deductPoint(command.senderId(), command.amount());
        if (!paid) {
            log.warn("포인트 차감 실패 - 후원 중단 (senderId={}, amount={})",
                    command.senderId(), command.amount());
            throw PAYMENT_FAILED.exception();
        }

        // 2) 후원 영속화
        Donations donations = Donations.builder()
                .streamId(command.streamId())
                .senderId(command.senderId())
                .receiverId(command.receiverId())
                .amount(command.amount())
                .message(command.message())
                .isBlocked(false)
                .isRefunded(false)
                .status(DonationStatus.COMPLETED)
                .build();

        try {
            donations = donationCommandPort.save(donations);
        } catch (RuntimeException e) {
            // TODO(보상): 차감된 포인트 환불 (payment 환불 RPC 연동)
            log.error("후원 저장 실패 - 포인트 차감분 보상 필요 (senderId={}, amount={})",
                    command.senderId(), command.amount(), e);
            throw e;
        }

        // 3) 시청자 실시간 알림 (커밋 후)
        donationNotifyUseCase.notifyDonation(donations, command.senderNickname());

        return donations.getId();
    }
}
