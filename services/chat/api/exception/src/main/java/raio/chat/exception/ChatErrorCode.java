package raio.chat.exception;

import raio.common.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Supplier;

public enum ChatErrorCode implements ErrorCode {
    // ===== 사용자 / 권한 =====
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_SESSION_NOT_FOUND("채팅 세션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_SESSION_FORBIDDEN("해당 채팅 세션에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 메시지 =====
    MESSAGE_NOT_FOUND("채팅 메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MESSAGE_FORBIDDEN("해당 메시지에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    MESSAGE_CONTENT_REQUIRED("메시지 내용은 필수입니다.", HttpStatus.BAD_REQUEST),
    MESSAGE_TOO_LONG("메시지 길이가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 요청 / payload =====
    PAYLOAD_REQUIRED("payload_json은 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYLOAD_FORMAT("payload 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE_TYPE("지원하지 않는 메시지 타입입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 채팅 상태 =====
    CHAT_ALREADY_CLOSED("이미 종료된 채팅 세션입니다.", HttpStatus.BAD_REQUEST),
    CHAT_NOT_ACTIVE("활성 상태가 아닌 채팅입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 파일 / 이미지 (썸네일, 상세 연계 고려) =====
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_FORMAT("지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 내부 =====
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
   
    private final String message;
    private final HttpStatus httpStatus;
    
    ChatErrorCode(String message, HttpStatus httpStatus) {
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
    public ChatException exception() {
        return new ChatException(this);
    }

    @Override
    public ChatException exception(Throwable cause) {
        return new ChatException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new ChatException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new ChatException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new ChatException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new ChatException(this, payload, cause);
    }
}