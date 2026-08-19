package raio.settlement.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import raio.settlement.application.port.SettlementCommandRepositoryPort;
import raio.settlement.domain.Settlement;
import raio.settlement.domain.SettlementItem;
import raio.settlement.adapter.rdb.mapper.SettlementEntityMapper;
import raio.settlement.adapter.rdb.mapper.SettlementItemEntityMapper;
import raio.settlement.adapter.rdb.repository.SettlementItemJpaRepository;
import raio.settlement.adapter.rdb.repository.SettlementJpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class SettlementCommandAdapter implements SettlementCommandRepositoryPort {

    private final SettlementJpaRepository settlementJpaRepository;
    private final SettlementItemJpaRepository settlementItemJpaRepository;
    private final SettlementEntityMapper settlementEntityMapper;
    private final SettlementItemEntityMapper settlementItemEntityMapper;

    @Override
    @Transactional
    public <T> T transaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public Optional<Settlement> findById(String id) {
        return settlementJpaRepository.findById(id)
                .map(settlementEntityMapper::toDomain);
    }

    @Override
    public Optional<Settlement> findByStreamerIdAndPeriod(String streamerId, Instant periodStartAt, Instant periodEndAt) {
        return settlementJpaRepository
                .findByStreamerIdAndPeriodStartAtAndPeriodEndAt(Long.parseLong(streamerId), periodStartAt, periodEndAt)
                .map(settlementEntityMapper::toDomain);
    }

    @Override
    public Settlement saveWithItems(Settlement settlement, List<SettlementItem> items) {
        var itemEntities = items.stream()
                .map(settlementItemEntityMapper::toEntity)
                .toList();
        settlementItemJpaRepository.saveAll(itemEntities);

        var saved = settlementJpaRepository.saveAndFlush(settlementEntityMapper.toEntity(settlement));
        return settlementEntityMapper.toDomain(saved);
    }

    @Override
    public Settlement save(Settlement settlement) {
        var saved = settlementJpaRepository.saveAndFlush(settlementEntityMapper.toEntity(settlement));
        return settlementEntityMapper.toDomain(saved);
    }
}
