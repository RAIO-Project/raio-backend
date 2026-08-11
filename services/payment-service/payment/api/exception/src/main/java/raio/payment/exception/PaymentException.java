package raio.payment.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class PaymentException extends CustomException {
    
    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
    
    public PaymentException(PaymentErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public PaymentException(PaymentErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }
    
    public PaymentException(PaymentErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }
    
    public PaymentException(PaymentErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }
    
    public PaymentException(PaymentErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
