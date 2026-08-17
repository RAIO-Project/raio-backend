package raio.settlement.application.port;

import raio.settlement.domain.Settlement;
import raio.settlement.domain.SettlementItem;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface SettlementCommandRepositoryPort {
    
    <T> T transaction(Supplier<T> supplier);
    
    Optional<Settlement> findById(String id);

    Optional<Settlement> findByStreamerIdAndPeriod(String streamerId, Instant periodStartAt, Instant periodEndAt);

    Settlement saveWithItems(Settlement settlement, List<SettlementItem> items);

    Settlement save(Settlement settlement);
}