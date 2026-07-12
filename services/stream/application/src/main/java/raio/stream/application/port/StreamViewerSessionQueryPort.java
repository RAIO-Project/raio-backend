package raio.stream.application.port;

/** 시청자 세션 매핑 조회 (driven). */
public interface StreamViewerSessionQueryPort {

    /** 이미 시청자로 집계된 세션인지. */
    boolean isBound(String sessionId);
}
