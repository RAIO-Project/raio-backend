package raio.stream.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class StreamException extends CustomException {
    
    public StreamException(StreamErrorCode errorCode) {
        super(errorCode);
    }
    
    public StreamException(StreamErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public StreamException(StreamErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }
    
    public StreamException(StreamErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }
    
    public StreamException(StreamErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }
    
    public StreamException(StreamErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
