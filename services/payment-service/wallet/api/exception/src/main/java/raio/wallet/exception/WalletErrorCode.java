package raio.wallet.exception;

import org.springframework.http.HttpStatus;
import raio.common.ErrorCode;
import java.util.Map;
import java.util.function.Supplier;

public enum WalletErrorCode implements ErrorCode {
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WALLET_FORBIDDEN("해당 지갑에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    WALLET_NOT_FOUND("지갑 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WALLET_ALREADY_EXISTS("이미 생성된 지갑입니다.", HttpStatus.CONFLICT),
    INVALID_POINT_AMOUNT("유효하지 않은 포인트 금액입니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_POINT_BALANCE("포인트 잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    POINT_HISTORY_NOT_FOUND("포인트 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_POINT_HISTORY_TYPE("유효하지 않은 포인트 이력 유형입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SOURCE_ID("유효하지 않은 원천 이벤트 ID입니다.", HttpStatus.BAD_REQUEST),
    SETTLEMENT_SERVICE_UNAVAILABLE("정산 서비스에 연결할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    SETTLEMENT_SERVICE_TIMEOUT("정산 서비스 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT);

    private final String message;
    
    private final HttpStatus httpStatus;
    
    WalletErrorCode(String message, HttpStatus httpStatus) { this.message = message; this.httpStatus = httpStatus; }
    
    @Override public String message() { return message; }
    
    @Override public HttpStatus httpStatus() { return httpStatus; }
    
    @Override public WalletException exception() { return new WalletException(this); }
    
    @Override public WalletException exception(Throwable cause) { return new WalletException(this, cause); }
    
    @Override public RuntimeException exception(Runnable runnable) { return new WalletException(this, runnable); }
    
    @Override public RuntimeException exception(Runnable runnable, Throwable cause) { return new WalletException(this, runnable, cause); }
    
    @Override public RuntimeException exception(Supplier<Map<String, Object>> payload) { return new WalletException(this, payload); }
    
    @Override public RuntimeException exception(Supplier<Map<String, Object>> payload, Throwable cause) { return new WalletException(this, payload, cause); }
}
