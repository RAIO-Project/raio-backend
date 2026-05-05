package raio.chat.exception;

import raio.common.CustomException;

import java.util.Map;
import java.util.function.Supplier;

public class ChatException extends CustomException {

    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }

    public ChatException(ChatErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChatException(ChatErrorCode errorCode, Runnable runnable) {
        super(errorCode, runnable);
    }

    public ChatException(ChatErrorCode errorCode, Runnable runnable, Throwable cause) {
        super(errorCode, runnable, cause);
    }

    public ChatException(ChatErrorCode errorCode, Supplier<Map<String, Object>> payload) {
        super(errorCode, payload);
    }

    public ChatException(ChatErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) {
        super(errorCode, payload, cause);
    }
}
