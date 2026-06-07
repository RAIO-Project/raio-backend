package raio.chat.socket.relay;

import java.time.Instant;

/**
 * Redis pub/sub 으로 인스턴스 간 전달되는 메시지 페이로드.
 * publish(ChatBroadcastAdapter) 와 subscribe(ChatRelaySubscriber) 가 공유한다.
 *
 * <p>JSON 직렬화/역직렬화되므로 record + 기본 타입만 사용.
 * type: "CHAT" | "JOIN" | "LEAVE"
 */
public record ChatRelayMessage(
        String type,
        String streamId,
        String userId,
        String nickname,
        String message,
        Boolean isBlocked,
        String blockReason,
        Instant occurredAt
) {
    public static ChatRelayMessage chat(
            String streamId, String userId, String nickname,
            String message, boolean isBlocked, String blockReason, Instant occurredAt) {
        return new ChatRelayMessage("CHAT", streamId, userId, nickname,
                message, isBlocked, blockReason, occurredAt);
    }

    public static ChatRelayMessage presence(
            String type, String streamId, String userId, String nickname, Instant occurredAt) {
        return new ChatRelayMessage(type, streamId, userId, nickname,
                null, null, null, occurredAt);
    }
}