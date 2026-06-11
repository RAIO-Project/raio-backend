package raio.chat.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

/**
 * 입장/퇴장 relay 페이로드. {@link RelayMessage} 구현.
 * JOIN/LEAVE 는 필드 구성이 같으므로 한 record 로 두고 {@link #type} 으로 구분한다.
 */
public record PresenceMessage(
        String type,
        String streamId,
        String userId,
        String nickname,
        Instant occurredAt
) implements RelayMessage {

    public static PresenceMessage join(String streamId, String userId, String nickname, Instant occurredAt) {
        return new PresenceMessage("JOIN", streamId, userId, nickname, occurredAt);
    }

    public static PresenceMessage leave(String streamId, String userId, String nickname, Instant occurredAt) {
        return new PresenceMessage("LEAVE", streamId, userId, nickname, occurredAt);
    }
}