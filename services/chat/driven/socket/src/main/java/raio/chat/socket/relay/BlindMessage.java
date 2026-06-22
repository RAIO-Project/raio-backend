package raio.chat.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

/** 채팅 블라인드 relay 페이로드. type="BLIND". 클라가 chatId 메시지를 가린다. */
public record BlindMessage(
        String streamId,
        String chatId,
        String reason,
        Instant occurredAt
) implements RelayMessage {

    @Override
    public String type() {
        return "BLIND";
    }
}