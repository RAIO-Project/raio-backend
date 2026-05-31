package raio.user.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum UserErrorCode implements ErrorCode {
    // ===== 사용자 =====
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    NICKNAME_ALREADY_EXISTS("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    
    // ===== 인증 =====
    INVALID_EMAIL_OR_PASSWORD("이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    PASSWORD_REQUIRED("비밀번호는 필수입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 상태 =====
    USER_NOT_ACTIVE("활성 상태의 사용자가 아닙니다.", HttpStatus.BAD_REQUEST),
    USER_SUSPENDED("정지된 사용자입니다.", HttpStatus.FORBIDDEN),
    USER_REMOVED("탈퇴한 사용자입니다.", HttpStatus.FORBIDDEN),
    
    // ===== 입력값 =====
    EMAIL_REQUIRED("이메일은 필수입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_REQUIRED("닉네임은 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE("유효하지 않은 사용자 권한입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 내부 =====
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),  ;
   
    private final String message;
    private final HttpStatus httpStatus;
    
    UserErrorCode(String message, HttpStatus httpStatus) {
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
    public UserException exception() {
        return new UserException(this);
    }

    @Override
    public UserException exception(Throwable cause) {
        return new UserException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new UserException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new UserException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new UserException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new UserException(this, payload, cause);
    }
}