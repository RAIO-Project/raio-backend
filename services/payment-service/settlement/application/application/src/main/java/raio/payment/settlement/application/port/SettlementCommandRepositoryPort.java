package raio.payment.settlement.application.port;

import raio.payment.settlement.domain.Settlement;
import raio.payment.settlement.domain.SettlementItem;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SettlementCommandRepositoryPort {

    Optional<Settlement> findById(String id);

    Optional<Settlement> findByStreamerIdAndPeriod(String streamerId, Instant periodStartAt, Instant periodEndAt);

    Settlement saveWithItems(Settlement settlement, List<SettlementItem> items);

    Settlement save(Settlement settlement);
}