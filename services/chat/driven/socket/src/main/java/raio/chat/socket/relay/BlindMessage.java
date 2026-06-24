package raio.chat.socket.relay;

import com.fasterxml.jackson.annotation.JsonProperty;
import raio.socket.relay.RelayMessage;

import java.time.Instant;

/** 채팅 블라인드 relay 페이로드. type="BLIND". */
public record BlindMessage(
        String streamId,
        String chatId,
        String reason,
        Instant occurredAt
) implements RelayMessage {

    @Override
    @JsonProperty("type")
    public String type() {
        return "BLIND";
    }
}