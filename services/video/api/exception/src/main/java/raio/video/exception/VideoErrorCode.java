package raio.video.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum VideoErrorCode implements ErrorCode {

    VIDEO_NOT_FOUND("동영상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_FILE_TYPE("지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    VideoErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public VideoException exception() {
        return new VideoException(this);
    }

    @Override
    public VideoException exception(Throwable cause) {
        return new VideoException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new VideoException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new VideoException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new VideoException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new VideoException(this, payload, cause);
    }
}
