package raio.settlement.adapter.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.settlement.domain.type.SettlementStatus;

import java.util.Arrays;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_INVALID_STATUS;

@Getter
@RequiredArgsConstructor
public enum SettlementStatusEntityType {

    CALCULATING((short) 1),
    CALCULATED((short) 2),
    CONFIRMED((short) 3),
    CANCELLED((short) 4);

    private final short code;

    public static SettlementStatusEntityType valueOf(SettlementStatus status) {
        return switch (status) {
            case CALCULATING -> CALCULATING;
            case CALCULATED -> CALCULATED;
            case CONFIRMED -> CONFIRMED;
            case CANCELLED -> CANCELLED;
        };
    }

    public static SettlementStatusEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(SETTLEMENT_INVALID_STATUS::exception);
    }

    public SettlementStatus toDomain() {
        return switch (this) {
            case CALCULATING -> SettlementStatus.CALCULATING;
            case CALCULATED -> SettlementStatus.CALCULATED;
            case CONFIRMED -> SettlementStatus.CONFIRMED;
            case CANCELLED -> SettlementStatus.CANCELLED;
        };
    }
}
