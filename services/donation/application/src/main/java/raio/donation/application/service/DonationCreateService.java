package raio.donation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.donation.application.port.DonationBroadcastPort;
import raio.donation.application.port.PaymentCommandPort;
import raio.donation.application.usecase.DonationCreateUseCase;
import raio.donation.domain.Donations;
import raio.donation.domain.type.DonationStatus;

/**
 * 후원 생성 플로우 (알림 흡수).
 * 1) 포인트 차감(PaymentCommandPort) — 현재 gRPC 미구현(통과), 추후 payment gRPC 연동
 * 2) Donations 생성 (TODO: 영속화 포트(DonationCommandPort) 연동)
 * 3) 시청자 실시간 알림(DonationBroadcastPort → Redis fan-out → /topic/streams/{id})
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DonationCreateService implements DonationCreateUseCase {

    private final PaymentCommandPort paymentCommandPort;
    private final DonationBroadcastPort donationBroadcastPort;
    // TODO: private final DonationCommandPort donationCommandPort;  // 후원 영속화

    @Override
    public Long create(DonationCreateCommand command) {
        // 1) 포인트 차감 (TODO: payment gRPC). 실패 시 후원 중단.
        boolean paid = paymentCommandPort.deductPoint(command.senderId(), command.amount());
        if (!paid) {
            log.warn("포인트 차감 실패 - 후원 중단 (senderId={})", command.senderId());
            // TODO: DonationException 으로 전환
            throw new IllegalStateException("포인트가 부족하거나 차감에 실패했습니다.");
        }

        // 2) 후원 도메인 생성 (TODO: DB 저장 후 id 채번)
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
        // TODO: donations = donationCommandPort.save(donations);

        // 3) 시청자 실시간 알림 (Redis fan-out)
        donationBroadcastPort.broadcastDonation(
                donations.getStreamId(), donations, command.senderNickname());

        return donations.getId();
    }
}