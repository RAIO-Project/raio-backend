package raio.settlement.domain.type;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public enum SettlementCycle {
    DAILY,
    WEEKLY,
    MONTHLY;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /**
     * 주어진 시각 이후 이 주기의 다음 정산 예정 시각을 계산한다.
     */
    public Instant nextBoundaryAfter(Instant from) {
        return switch (this) {
            case DAILY -> from.plus(1, ChronoUnit.DAYS);
            case WEEKLY -> from.plus(7, ChronoUnit.DAYS);
            case MONTHLY -> from.atZone(ZONE).plusMonths(1).toInstant();
        };
    }
}
