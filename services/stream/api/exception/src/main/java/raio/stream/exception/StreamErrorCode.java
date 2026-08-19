package raio.stream.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum StreamErrorCode implements ErrorCode {
    // ===== 인증 / 권한 =====
    STREAM_UNAUTHORIZED("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    STREAM_FORBIDDEN("해당 방송에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // ===== 조회 =====
    STREAM_NOT_FOUND("방송을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_STREAM_CATEGORY("유효하지 않은 방송 카테고리입니다.", HttpStatus.BAD_REQUEST),

    // ===== 상태 =====
    STREAM_NOT_ACTIVE("현재 활성화된 방송이 아닙니다.", HttpStatus.BAD_REQUEST),
    STREAM_ALREADY_ENDED("이미 종료된 방송입니다.", HttpStatus.BAD_REQUEST),
    STREAM_ALREADY_STARTED("이미 시작된 방송입니다.", HttpStatus.BAD_REQUEST),

    // ===== 생성 / 수정 =====
    STREAM_TITLE_REQUIRED("방송 제목은 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_STREAM_STATUS("유효하지 않은 방송 상태입니다.", HttpStatus.BAD_REQUEST),

    // ===== 동영상 =====
    VIDEO_NOT_FOUND("동영상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_FILE_TYPE("지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== 내부 =====
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String message;
    private final HttpStatus httpStatus;

    StreamErrorCode(String message, HttpStatus httpStatus) {
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
    public StreamException exception() {
        return new StreamException(this);
    }

    @Override
    public StreamException exception(Throwable cause) {
        return new StreamException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new StreamException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new StreamException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new StreamException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new StreamException(this, payload, cause);
    }
}