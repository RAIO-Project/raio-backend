package raio.settlement.application.command;

import raio.settlement.domain.type.SettlementCycle;
import java.time.Instant;

public final class SettlementCommands {
    
    public record SettlementCalculateCommand(
            String streamerId,
            Instant periodStartAt,
            Instant periodEndAt
    ) {
    }
    
    public record SettlementCycleChangeCommand(
            String streamerId,
            SettlementCycle newCycle,
            Instant effectiveAt
    ) {
    }
}
