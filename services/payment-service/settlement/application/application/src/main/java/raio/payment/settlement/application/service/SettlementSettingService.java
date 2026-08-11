package raio.payment.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementSettingReadModel;
import raio.payment.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;
import raio.payment.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.payment.settlement.application.port.SettlementSettingQueryRepositoryPort;
import raio.payment.settlement.application.usecase.SettlementSettingUseCase;
import raio.payment.settlement.domain.SettlementSetting;

import java.time.Instant;

import static raio.payment.exception.PaymentErrorCode.SETTLEMENT_SETTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementSettingService implements SettlementSettingUseCase {

    private final SettlementSettingCommandRepositoryPort settlementSettingCommandRepositoryPort;
    private final SettlementSettingQueryRepositoryPort settlementSettingQueryRepositoryPort;

    @Override
    public SettlementSettingReadModel getSettlementSetting(String streamerId) {
        return settlementSettingQueryRepositoryPort.findSettlementSettingByStreamerId(streamerId)
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);
    }

    @Override
    @Transactional
    public SettlementSettingReadModel changeCycle(SettlementCycleChangeCommand command) {
        SettlementSetting setting = settlementSettingCommandRepositoryPort.findByStreamerId(command.streamerId())
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        setting.requestCycleChange(command.newCycle(), command.effectiveAt(), Instant.now());

        return toReadModel(settlementSettingCommandRepositoryPort.save(setting));
    }

    @Override
    @Transactional
    public SettlementSettingReadModel cancelCycleChange(String streamerId) {
        SettlementSetting setting = settlementSettingCommandRepositoryPort.findByStreamerId(streamerId)
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        setting.cancelPendingCycleChange(Instant.now());

        return toReadModel(settlementSettingCommandRepositoryPort.save(setting));
    }

    private SettlementSettingReadModel toReadModel(SettlementSetting setting) {
        return new SettlementSettingReadModel(
                setting.getStreamerId(),
                setting.getCurrentCycle(),
                setting.getPendingCycle(),
                setting.getPendingCycleEffectiveAt(),
                setting.isActive()
        );
    }
}
