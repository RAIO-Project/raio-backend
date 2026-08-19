package raio.settlement.adapter.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.settlement.domain.type.SettlementCycle;

import java.util.Arrays;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_INVALID_CYCLE;

@Getter
@RequiredArgsConstructor
public enum SettlementCycleEntityType {

    DAILY((short) 1),
    WEEKLY((short) 2),
    MONTHLY((short) 3);

    private final short code;

    public static SettlementCycleEntityType valueOf(SettlementCycle cycle) {
        return switch (cycle) {
            case DAILY -> DAILY;
            case WEEKLY -> WEEKLY;
            case MONTHLY -> MONTHLY;
        };
    }

    public static SettlementCycleEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(SETTLEMENT_INVALID_CYCLE::exception);
    }

    public SettlementCycle toDomain() {
        return switch (this) {
            case DAILY -> SettlementCycle.DAILY;
            case WEEKLY -> SettlementCycle.WEEKLY;
            case MONTHLY -> SettlementCycle.MONTHLY;
        };
    }
}
