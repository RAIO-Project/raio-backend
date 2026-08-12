package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;
import raio.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.settlement.application.port.SettlementSettingQueryRepositoryPort;
import raio.settlement.application.usecase.SettlementSettingUseCase;

import java.time.Instant;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_SETTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementSettingService implements SettlementSettingUseCase {

    private final SettlementSettingCommandRepositoryPort settlementSettingCommandRepositoryPort;
    private final SettlementSettingQueryRepositoryPort settlementSettingQueryRepositoryPort;

    @Override
    public SettlementSettingSummary getSettlementSetting(String streamerId) {
        return settlementSettingQueryRepositoryPort.findSettlementSettingByStreamerId(streamerId)
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);
    }

    @Override
    @Transactional
    public SettlementSettingSummary changeCycle(SettlementCycleChangeCommand command) {
        var setting = settlementSettingCommandRepositoryPort.findByStreamerId(command.streamerId())
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        setting.requestCycleChange(command.newCycle(), command.effectiveAt(), Instant.now());

        return toReadModel(settlementSettingCommandRepositoryPort.save(setting));
    }

    @Override
    @Transactional
    public SettlementSettingSummary cancelCycleChange(String streamerId) {
        var setting = settlementSettingCommandRepositoryPort.findByStreamerId(streamerId)
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        setting.cancelPendingCycleChange(Instant.now());

        return toReadModel(settlementSettingCommandRepositoryPort.save(setting));
    }

    private SettlementSettingSummary toReadModel(SettlementSetting setting) {
        return new SettlementSettingSummary(
                setting.getStreamerId(),
                setting.getCurrentCycle(),
                setting.getPendingCycle(),
                setting.getPendingCycleEffectiveAt(),
                setting.isActive()
        );
    }
}
