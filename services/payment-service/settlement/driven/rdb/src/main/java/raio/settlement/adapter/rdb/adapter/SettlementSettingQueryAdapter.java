package raio.settlement.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.application.port.SettlementSettingQueryRepositoryPort;
import raio.settlement.adapter.rdb.mapper.SettlementSettingEntityMapper;
import raio.settlement.adapter.rdb.repository.SettlementSettingJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementSettingQueryAdapter implements SettlementSettingQueryRepositoryPort {

    private final SettlementSettingJpaRepository settlementSettingJpaRepository;
    private final SettlementSettingEntityMapper settlementSettingEntityMapper;

    @Override
    public Optional<SettlementSettingSummary> findSettlementSettingByStreamerId(String streamerId) {
        return settlementSettingJpaRepository.findById(Long.parseLong(streamerId))
                .map(settlementSettingEntityMapper::toSummary);
    }
}
