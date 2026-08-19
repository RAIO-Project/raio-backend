package raio.donation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.donation.application.port.DonationCommandPort;
import raio.donation.application.port.WalletCommandPort;
import raio.donation.application.usecase.DonationCreateUseCase;
import raio.donation.application.usecase.DonationNotifyUseCase;
import raio.donation.domain.Donations;
import raio.donation.domain.type.DonationStatus;

import java.util.UUID;

import static raio.donation.exception.DonationErrorCode.PAYMENT_FAILED;

/**
 * 후원 생성 플로우.
 * 1) 포인트 차감 ({@link WalletCommandPort}) — 실패 시 후원 중단
 * 2) 후원 영속화 ({@link DonationCommandPort}) — id 채번
 * 3) 시청자 실시간 알림 — {@link DonationNotifyUseCase} 에 위임
 *
 * <p>차감은 wallet에서 확정되므로 이 서비스의 트랜잭션에 묶이지 않는다. 2)가 실패하면
 * 로컬 롤백만으로는 이미 빠져나간 포인트를 되돌릴 수 없으므로, 역방향 호출로 보상해야 한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationCommandService implements DonationCreateUseCase {

    /** 보상으로 발생한 충전 이력을 실제 결제 충전과 구분하기 위한 sourceId 접두사. */
    private static final String COMPENSATION_SOURCE_PREFIX = "DONATION_COMPENSATION:";

    private final WalletCommandPort walletCommandPort;
    private final DonationCommandPort donationCommandPort;
    private final DonationNotifyUseCase donationNotifyUseCase;

    @Override
    public Long create(DonationCreateCommand command) {
        // 이 후원 시도를 식별하는 키. 보상 호출의 멱등키로 쓴다.
        // 후원 id 는 저장에 성공해야 생기므로, 저장 실패 시점에도 쓸 수 있는 키가 따로 필요하다.
        // 보상은 wallet 의 충전 이력으로 남으므로, 실제 결제 충전과 구분되도록 접두사를 붙인다.
        String attemptId = COMPENSATION_SOURCE_PREFIX + UUID.randomUUID();

        // 1) 포인트 차감
        boolean paid = walletCommandPort.deductPoint(command.senderId(), command.amount());
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
            compensate(command, attemptId, e);
            throw e;
        }

        // 3) 시청자 실시간 알림 (커밋 후)
        donationNotifyUseCase.notifyDonation(donations, command.senderNickname());

        return donations.getId();
    }

    /**
     * 차감된 포인트를 되돌린다.
     *
     * <p>보상 호출 자체도 실패할 수 있다. 그 경우 포인트가 빠진 채로 남으므로 로그 레벨을 높여
     * 수동 개입 대상임을 분명히 남긴다. 여기서 예외를 다시 던지지 않는 이유는, 호출자에게는 원래의
     * 저장 실패 원인이 전달돼야 하기 때문이다.
     */
    private void compensate(DonationCreateCommand command, String attemptId, RuntimeException cause) {
        log.error("후원 저장 실패 - 포인트 보상 시도 (senderId={}, amount={}, attemptId={})",
                command.senderId(), command.amount(), attemptId, cause);

        boolean refunded;
        try {
            refunded = walletCommandPort.refundPoint(command.senderId(), command.amount(), attemptId);
        } catch (RuntimeException e) {
            log.error("[포인트 보상 실패(MANUAL_INTERVENTION_REQUIRED)] senderId={}, amount={}, attemptId={}",
                    command.senderId(), command.amount(), attemptId, e);
            return;
        }

        if (!refunded) {
            log.error("[포인트 보상 실패(MANUAL_INTERVENTION_REQUIRED)] senderId={}, amount={}, attemptId={}",
                    command.senderId(), command.amount(), attemptId);
        }
    }
}
