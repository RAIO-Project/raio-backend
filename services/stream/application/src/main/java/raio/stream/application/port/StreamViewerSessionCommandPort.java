package raio.stream.application.port;

/**
 * 시청자로 집계된 WebSocket 세션 ↔ 스트림 매핑 갱신 (driven).
 */
public interface StreamViewerSessionCommandPort {

    /** 집계된 세션을 스트림에 묶는다. */
    void bind(String sessionId, String streamId);

    /** 세션 매핑 해제. 묶여 있던 streamId 반환 (없으면 null). */
    String unbind(String sessionId);
}
