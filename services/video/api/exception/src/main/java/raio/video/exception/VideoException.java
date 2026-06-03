package raio.video.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class VideoException extends CustomException {

    public VideoException(VideoErrorCode errorCode) {
        super(errorCode);
    }

    public VideoException(VideoErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public VideoException(VideoErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }

    public VideoException(VideoErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }

    public VideoException(VideoErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }

    public VideoException(VideoErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
