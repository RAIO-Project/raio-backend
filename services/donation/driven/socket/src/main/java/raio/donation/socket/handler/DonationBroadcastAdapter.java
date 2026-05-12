package raio.donation.socket.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import raio.donation.application.port.DonationBroadcastPort;
import raio.donation.domain.Donations;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DonationBroadcastAdapter implements DonationBroadcastPort {
    private static final String TOPIC = "/topic/streams/";
    private final SimpMessagingTemplate messaging;

    @Override
    public void broadcastDonation(Long streamId, Donations donations, String senderNickname) {
        messaging.convertAndSend(TOPIC + streamId,
                new DonationPayload(
                        "DONATION",
                        String.valueOf(donations.getStreamId()),
                        String.valueOf(donations.getSenderId()),
                        senderNickname,
                        donations.getAmount(),
                        donations.getMessage(),
                        donations.isBlocked(),
                        Instant.now()
                ));
    }

    // 이 어댑터 전용 — 외부에 노출 안 됨
    private record DonationPayload(
            String type,
            String streamId,
            String senderId,
            String senderNickname,
            Long amount,
            String message,
            Boolean isBlocked,
            Instant createdAt
    ) {}
}
