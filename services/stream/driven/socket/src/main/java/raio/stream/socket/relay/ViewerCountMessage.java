package raio.stream.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

/**
 * 시청자 수 relay 페이로드. {@link RelayMessage} 구현.
 * type 을 record 컴포넌트로 두어 Jackson 직렬화에서 누락되지 않게 한다.
 */
public record ViewerCountMessage(
        String type,
        String streamId,
        long viewerCount,
        Instant occurredAt
) implements RelayMessage {

    public static ViewerCountMessage of(String streamId, long viewerCount, Instant occurredAt) {
        return new ViewerCountMessage("VIEWER_COUNT", streamId, viewerCount, occurredAt);
    }
}
