package raio.donation.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

/**
 * 후원 메시지 relay 페이로드. {@link RelayMessage} 구현.
 * publish(DonationBroadcastAdapter)가 공용 채널로 발행, core RelaySubscriber 가 전달한다.
 */
public record DonationMessage(
        String streamId,
        String senderId,
        String senderNickname,
        Long amount,
        String message,
        boolean isBlocked,
        Instant occurredAt
) implements RelayMessage {

    @Override
    public String type() {
        return "DONATION";
    }
}