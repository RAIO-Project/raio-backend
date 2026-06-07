package raio.payment.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.payment.domain.type.PaymentStatus;

import java.util.Arrays;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_INVALID_STATUS;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusEntityType {

    READY((short) 1),
    IN_PROGRESS((short) 2),
    DONE((short) 3),
    FAILED((short) 4),
    CANCELED((short) 5);

    private final short code;

    public static PaymentStatusEntityType valueOf(PaymentStatus status) {
        return switch (status) {
            case READY -> READY;
            case IN_PROGRESS -> IN_PROGRESS;
            case DONE -> DONE;
            case FAILED -> FAILED;
            case CANCELED -> CANCELED;
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
            case READY -> PaymentStatus.READY;
            case IN_PROGRESS -> PaymentStatus.IN_PROGRESS;
            case DONE -> PaymentStatus.DONE;
            case FAILED -> PaymentStatus.FAILED;
            case CANCELED -> PaymentStatus.CANCELED;
        };
    }
}
