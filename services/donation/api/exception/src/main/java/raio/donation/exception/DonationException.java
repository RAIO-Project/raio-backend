package raio.donation.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class DonationException extends CustomException {
    
    public DonationException(DonationErrorCode errorCode) {
        super(errorCode);
    }
    
    public DonationException(DonationErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public DonationException(DonationErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }
    
    public DonationException(DonationErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }
    
    public DonationException(DonationErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }
    
    public DonationException(DonationErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
