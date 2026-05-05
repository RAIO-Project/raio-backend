package raio.donation.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum DonationErrorCode implements ErrorCode {
    // ===== 사용자 / 권한 =====
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DONATION_FORBIDDEN("해당 후원에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 후원 =====
    DONATION_NOT_FOUND("후원 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_DONATION_AMOUNT("유효하지 않은 후원 금액입니다.", HttpStatus.BAD_REQUEST),
    DONATION_AMOUNT_REQUIRED("후원 금액은 필수입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 결제 / 상태 =====
    PAYMENT_FAILED("결제 처리에 실패했습니다.", HttpStatus.BAD_REQUEST),
    DONATION_ALREADY_REFUNDED("이미 환불된 후원입니다.", HttpStatus.BAD_REQUEST),
    REFUND_FAILED("환불 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ===== 스트림 =====
    STREAM_NOT_FOUND("방송을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STREAM_NOT_ACTIVE("현재 후원이 불가능한 방송입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 메시지 =====
    MESSAGE_TOO_LONG("후원 메시지 길이가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 내부 =====
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
   
    private final String message;
    private final HttpStatus httpStatus;
    
    DonationErrorCode(String message, HttpStatus httpStatus) {
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
    public DonationException exception() {
        return new DonationException(this);
    }

    @Override
    public DonationException exception(Throwable cause) {
        return new DonationException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new DonationException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new DonationException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new DonationException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new DonationException(this, payload, cause);
    }
}