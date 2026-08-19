package raio.wallet.exception;

import raio.common.CustomException;
import java.util.Map;
import java.util.function.Supplier;

public class WalletException extends CustomException {
    public WalletException(WalletErrorCode errorCode) { super(errorCode); }
    public WalletException(WalletErrorCode errorCode, Throwable cause) { super(errorCode, cause); }
    public WalletException(WalletErrorCode errorCode, Runnable runnable) { super(errorCode, runnable); }
    public WalletException(WalletErrorCode errorCode, Runnable runnable, Throwable cause) { super(errorCode, runnable, cause); }
    public WalletException(WalletErrorCode errorCode, Supplier<Map<String, Object>> payload) { super(errorCode, payload); }
    public WalletException(WalletErrorCode errorCode, Supplier<Map<String, Object>> payload, Throwable cause) { super(errorCode, payload, cause); }
}
