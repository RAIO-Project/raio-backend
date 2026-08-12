package raio.settlement.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.adapter.rdb.mapper.SettlementSettingEntityMapper;
import raio.settlement.adapter.rdb.repository.SettlementSettingJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementSettingCommandAdapter implements SettlementSettingCommandRepositoryPort {

    private final SettlementSettingJpaRepository settlementSettingJpaRepository;
    private final SettlementSettingEntityMapper settlementSettingEntityMapper;

    @Override
    public Optional<SettlementSetting> findByStreamerId(String streamerId) {
        return settlementSettingJpaRepository.findById(Long.parseLong(streamerId))
                .map(settlementSettingEntityMapper::toDomain);
    }

    @Override
    public SettlementSetting save(SettlementSetting setting) {
        var saved = settlementSettingJpaRepository.saveAndFlush(settlementSettingEntityMapper.toEntity(setting));
        return settlementSettingEntityMapper.toDomain(saved);
    }
}
