package raio.payment.settlement.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;
import java.util.Map;
import java.util.function.Supplier;

public enum SettlementErrorCode implements ErrorCode {
    SETTLEMENT_NOT_FOUND("정산 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SETTLEMENT_ALREADY_EXISTS("이미 생성된 정산입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_INVALID_STATUS("유효하지 않은 정산 상태입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_AMOUNT("유효하지 않은 정산 금액입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_AMOUNT_MISMATCH("정산 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_TARGET_NOT_FOUND("정산 대상이 없습니다.", HttpStatus.NOT_FOUND),
    SETTLEMENT_PERIOD_INVALID("유효하지 않은 정산 기간입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_CYCLE("유효하지 않은 정산 주기입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_INVALID_FEE_RATE("유효하지 않은 수수료율입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_ITEM_OUT_OF_PERIOD("정산 기간을 벗어난 후원 내역입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_ITEM_FEE_RATE_MISMATCH("정산 항목의 수수료율이 정산 기준과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_DUPLICATED_DONATION("이미 정산에 포함된 후원 내역입니다.", HttpStatus.CONFLICT),
    SETTLEMENT_CYCLE_CHANGE_INVALID("유효하지 않은 정산 주기 변경 요청입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_SETTING_NOT_FOUND("정산 설정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
    SettlementErrorCode(String message, HttpStatus httpStatus) { this.message = message; this.httpStatus = httpStatus; }
    @Override public String message() { return message; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
    @Override public SettlementException exception() { return new SettlementException(this); }
    @Override public SettlementException exception(Throwable cause) { return new SettlementException(this, cause); }
    @Override public RuntimeException exception(Runnable runnable) { return new SettlementException(this, runnable); }
    @Override public RuntimeException exception(Runnable runnable, Throwable cause) { return new SettlementException(this, runnable, cause); }
    @Override public RuntimeException exception(Supplier<Map<String, Object>> payload) { return new SettlementException(this, payload); }
    @Override public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) { return new SettlementException(this, payload, cause); }
}
