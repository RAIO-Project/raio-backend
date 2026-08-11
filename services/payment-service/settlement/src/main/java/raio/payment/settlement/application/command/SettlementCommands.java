package raio.payment.settlement.application.command;

import raio.payment.settlement.domain.type.SettlementCycle;

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
