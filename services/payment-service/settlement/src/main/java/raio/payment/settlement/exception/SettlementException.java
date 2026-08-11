package raio.payment.settlement.exception;

import raio.common.CustomException;
import java.util.Map;
import java.util.function.Supplier;

public class SettlementException extends CustomException {
    public SettlementException(SettlementErrorCode errorCode) { super(errorCode); }
    public SettlementException(SettlementErrorCode errorCode, Throwable cause) { super(errorCode, cause); }
    public SettlementException(SettlementErrorCode errorCode, Runnable runnable) { super(errorCode, runnable); }
    public SettlementException(SettlementErrorCode errorCode, Runnable runnable, Throwable cause) { super(errorCode, runnable, cause); }
    public SettlementException(SettlementErrorCode errorCode, Supplier<Map<String, Object>> payload) { super(errorCode, payload); }
    public SettlementException(SettlementErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) { super(errorCode, payload, cause); }
}
