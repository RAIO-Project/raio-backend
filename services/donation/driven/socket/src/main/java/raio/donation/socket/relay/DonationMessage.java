package raio.donation.socket.relay;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("type")   // 추가 — Jackson이 type을 JSON 필드로 직렬화
    @Override
    public String type() {
        return "DONATION";
    }
}