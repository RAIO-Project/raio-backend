package raio.chat.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

/** 실시간 시청자 수 relay 페이로드. type="VIEWER_COUNT". 프론트가 받아서 숫자 갱신. */
public record ViewerCountMessage(
        String streamId,
        long count,
        Instant occurredAt
) implements RelayMessage {

    @Override
    public String type() {
        return "VIEWER_COUNT";
    }
}