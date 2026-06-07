package raio.payment.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.payment.domain.type.PaymentMethod;

import java.util.Arrays;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_INVALID_METHOD;

@Getter
@RequiredArgsConstructor
public enum PaymentMethodEntityType {

    CARD((short) 1),
    VIRTUAL_ACCOUNT((short) 2),
    EASY_PAY((short) 3),
    TRANSFER((short) 4),
    MOBILE_PHONE((short) 5);

    private final short code;

    public static PaymentMethodEntityType valueOf(PaymentMethod method) {
        return switch (method) {
            case CARD -> CARD;
            case VIRTUAL_ACCOUNT -> VIRTUAL_ACCOUNT;
            case EASY_PAY -> EASY_PAY;
            case TRANSFER -> TRANSFER;
            case MOBILE_PHONE -> MOBILE_PHONE;
        };
    }

    public static PaymentMethodEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(t -> t.code == code)
                .findFirst()
                .orElseThrow(PAYMENT_INVALID_METHOD::exception);
    }

    public PaymentMethod toDomain() {
        return switch (this) {
            case CARD -> PaymentMethod.CARD;
            case VIRTUAL_ACCOUNT -> PaymentMethod.VIRTUAL_ACCOUNT;
            case EASY_PAY -> PaymentMethod.EASY_PAY;
            case TRANSFER -> PaymentMethod.TRANSFER;
            case MOBILE_PHONE -> PaymentMethod.MOBILE_PHONE;
        };
    }
}
