package raio.payment.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.payment.domain.type.PaymentStatus;

import java.util.Arrays;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_INVALID_STATUS;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusEntityType {

    PENDING((short) 1),
    SUCCESS((short) 2),
    FAIL((short) 3);

    private final short code;

    public static PaymentStatusEntityType valueOf(PaymentStatus status) {
        return switch (status) {
            case PENDING -> PENDING;
            case SUCCESS -> SUCCESS;
            case FAIL -> FAIL;
        };
    }

    public static PaymentStatusEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(t -> t.code == code)
                .findFirst()
                .orElseThrow(PAYMENT_INVALID_STATUS::exception);
    }

    public PaymentStatus toDomain() {
        return switch (this) {
            case PENDING -> PaymentStatus.PENDING;
            case SUCCESS -> PaymentStatus.SUCCESS;
            case FAIL -> PaymentStatus.FAIL;
        };
    }
}
