package raio.payment.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.payment.domain.type.PointHistoryType;

import java.util.Arrays;

import static raio.payment.exception.PaymentErrorCode.INVALID_POINT_HISTORY_TYPE;

@Getter
@RequiredArgsConstructor
public enum PointHistoryEntityType {
    
    CHARGE((short) 1),
    PAYMENT((short) 2),
    REFUND((short) 3);
    
    private final short code;
    
    public static PointHistoryEntityType valueOf(PointHistoryType type) {
        return switch (type) {
            case CHARGE -> CHARGE;
            case PAYMENT -> PAYMENT;
            case REFUND -> REFUND;
        };
    }
    
    public static PointHistoryEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(INVALID_POINT_HISTORY_TYPE::exception);
    }
    
    public PointHistoryType toDomain() {
        return switch (this) {
            case CHARGE -> PointHistoryType.CHARGE;
            case PAYMENT -> PointHistoryType.PAYMENT;
            case REFUND -> PointHistoryType.REFUND;
        };
    }
}