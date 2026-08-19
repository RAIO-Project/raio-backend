package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;
import raio.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.settlement.application.usecase.SettlementSettingCreateUseCase;
import raio.settlement.application.usecase.SettlementSettingUpdateUseCase;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.time.Instant;

import static raio.settlement.domain.type.SettlementCycle.MONTHLY;
import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_SETTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementSettingCommandService implements SettlementSettingCreateUseCase, SettlementSettingUpdateUseCase {

    private final SettlementSettingCommandRepositoryPort settlementSettingCommandRepositoryPort;
    
    @Override
    public SettlementSetting createSettlementSetting(String streamerId) {
        // 스트리머는 신규 정산 세팅 시, 정산 주기는 기본 월로 지정한다.
        var newSetting = SettlementSetting.create(streamerId, MONTHLY, Instant.now());

        return settlementSettingCommandRepositoryPort.save(newSetting);
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
                setting.getNextSettlementAt(),
                setting.isActive()
        );
    }
}
