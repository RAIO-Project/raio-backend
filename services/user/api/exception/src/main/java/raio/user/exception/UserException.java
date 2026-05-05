package raio.user.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class UserException extends CustomException {
    
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
    
    public UserException(UserErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public UserException(UserErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }
    
    public UserException(UserErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }
    
    public UserException(UserErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }
    
    public UserException(UserErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
