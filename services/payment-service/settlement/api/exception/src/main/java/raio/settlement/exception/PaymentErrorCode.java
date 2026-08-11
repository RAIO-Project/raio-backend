package raio.payment.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;

import java.util.Map;
import java.util.function.Supplier;

public enum PaymentErrorCode implements ErrorCode {
    // ===== 사용자 / 권한 =====
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    SETTLEMENT_NOT_FOUND("정산 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SETTLEMENT_DETAIL_NOT_FOUND("정산 상세 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SETTLEMENT_ALREADY_EXISTS("이미 생성된 정산입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_ALREADY_COMPLETED("이미 완료된 정산입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_STATUS("유효하지 않은 정산 상태입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_AMOUNT("유효하지 않은 정산 금액입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_AMOUNT_MISMATCH("정산 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_TARGET_NOT_FOUND("정산 대상이 없습니다.", HttpStatus.NOT_FOUND),
    SETTLEMENT_DUPLICATED_HISTORY("이미 정산에 포함된 포인트 이력입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_PERIOD_INVALID("유효하지 않은 정산 기간입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_BATCH_ALREADY_RUNNING("정산 배치가 이미 실행 중입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_BATCH_FAILED("정산 배치 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SETTLEMENT_BATCH_PARTIAL_FAILED("일부 정산 배치 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SETTLEMENT_INVALID_CYCLE("유효하지 않은 정산 주기입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_FEE_RATE("유효하지 않은 수수료율입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_ITEM_OUT_OF_PERIOD("정산 기간을 벗어난 후원 내역입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_ITEM_FEE_RATE_MISMATCH("정산 항목의 수수료율이 정산 기준과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_DUPLICATED_DONATION("이미 정산에 포함된 후원 내역입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_CYCLE_CHANGE_INVALID("유효하지 않은 정산 주기 변경 요청입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_SETTING_NOT_FOUND("정산 설정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
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