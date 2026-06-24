package raio.chat.socket.relay;

import com.fasterxml.jackson.annotation.JsonProperty;
import raio.socket.relay.RelayMessage;

import java.time.Instant;

/**
 * 채팅 메시지 relay 페이로드. {@link RelayMessage} 구현.
 * publish(ChatBroadcastAdapter) / subscribe(ChatRelaySubscriber) 가 공유.
 */
public record ChatMessage(
        String streamId,
        String chatId,
        String userId,
        String nickname,
        String message,
        boolean isBlocked,
        String blockReason,
        Instant occurredAt
) implements RelayMessage {

    @Override
    @JsonProperty("type")
    public String type() {
        return "CHAT";
    }
}