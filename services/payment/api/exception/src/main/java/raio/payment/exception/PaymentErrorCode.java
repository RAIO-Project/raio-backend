package raio.payment.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum PaymentErrorCode implements ErrorCode {
    // ===== 사용자 / 권한 =====
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WALLET_FORBIDDEN("해당 지갑에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    
    // ===== 지갑 / 포인트 =====
    WALLET_NOT_FOUND("지갑 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WALLET_ALREADY_EXISTS("이미 생성된 지갑입니다.", HttpStatus.CONFLICT),
    INVALID_POINT_AMOUNT("유효하지 않은 포인트 금액입니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_POINT_BALANCE("포인트 잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    POINT_HISTORY_NOT_FOUND("포인트 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_POINT_HISTORY_TYPE("유효하지 않은 포인트 이력 유형입니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 결제 =====
    PAYMENT_NOT_FOUND("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_PROCESSED("이미 처리된 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_STATUS("유효하지 않은 결제 상태입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_METHOD("유효하지 않은 결제 수단입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_INVALID_PG_PROVIDER("유효하지 않은 PG사입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_EXPIRED("결제 가능 시간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_AMOUNT_MISMATCH("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    
    // ===== 결제 승인 / 취소 =====
    PAYMENT_CONFIRM_FAILED("결제 승인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_CANCEL_FAILED("결제 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_REFUND_FAILED("결제 환불에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // ===== 내부 =====
    INTERNAL_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
   
    private final String message;
    private final HttpStatus httpStatus;
    
    PaymentErrorCode(String message, HttpStatus httpStatus) {
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
    public PaymentException exception() {
        return new PaymentException(this);
    }

    @Override
    public PaymentException exception(Throwable cause) {
        return new PaymentException(this, cause);
    }

    @Override
    public RuntimeException exception(Runnable runnable) {
        return new PaymentException(this, runnable);
    }

    @Override
    public RuntimeException exception(Runnable runnable, Throwable cause) {
        return new PaymentException(this, runnable, cause);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload) {
        return new PaymentException(this, payload);
    }

    @Override
    public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) {
        return new PaymentException(this, payload, cause);
    }
}