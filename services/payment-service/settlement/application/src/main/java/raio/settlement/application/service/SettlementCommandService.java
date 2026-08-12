package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.settlement.application.command.SettlementCommands.SettlementCalculateCommand;
import raio.settlement.application.port.SettlementCommandRepositoryPort;
import raio.settlement.application.port.SettlementDonationQueryPort;
import raio.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.settlement.application.usecase.SettlementCalculateUseCase;
import raio.settlement.application.usecase.SettlementCancelUseCase;
import raio.settlement.application.usecase.SettlementConfirmUseCase;
import raio.settlement.domain.Settlement;
import raio.settlement.domain.SettlementItem;
import raio.settlement.domain.policy.SettlementFeeContext;
import raio.settlement.domain.policy.SettlementFeePolicy;
import raio.settlement.domain.type.SettlementCycle;
import raio.settlement.domain.type.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_ALREADY_EXISTS;
import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_NOT_FOUND;
import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_SETTING_NOT_FOUND;
import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_TARGET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementCommandService implements SettlementCalculateUseCase, SettlementConfirmUseCase, SettlementCancelUseCase {

    private final SettlementCommandRepositoryPort settlementCommandRepositoryPort;
    private final SettlementSettingCommandRepositoryPort settlementSettingCommandRepositoryPort;
    private final SettlementDonationQueryPort settlementDonationQueryPort;
    private final SettlementFeePolicy settlementFeePolicy;

    @Override
    @Transactional
    public Settlement calculate(SettlementCalculateCommand command) {
        var existing = settlementCommandRepositoryPort.findByStreamerIdAndPeriod(
                command.streamerId(), command.periodStartAt(), command.periodEndAt());

        if (existing.isPresent()) {
            return resumeOrReturn(existing.get());
        }

        var setting = settlementSettingCommandRepositoryPort.findByStreamerId(command.streamerId())
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        SettlementCycle cycle = setting.resolveCycleFor(command.periodStartAt());

        var donations = settlementDonationQueryPort.findUnsettledDonations(
                command.streamerId(), command.periodStartAt(), command.periodEndAt());

        if (donations.isEmpty()) {
            throw SETTLEMENT_TARGET_NOT_FOUND.exception();
        }

        Instant now = Instant.now();
        BigDecimal feeRate = settlementFeePolicy.resolveFeeRate(
                new SettlementFeeContext(command.streamerId(), cycle, now));

        String settlementId = UUID.randomUUID().toString();

        List<SettlementItem> items = donations.stream()
                .map(donation -> SettlementItem.create(
                        UUID.randomUUID().toString(),
                        settlementId,
                        donation.donationId(),
                        donation.grossAmount(),
                        feeRate,
                        donation.occurredAt()))
                .toList();

        Settlement settlement = Settlement.calculate(
                settlementId,
                command.streamerId(),
                cycle,
                command.periodStartAt(),
                command.periodEndAt(),
                feeRate,
                items,
                now);

        // 정산 항목과 함께 CALCULATING 상태로 우선 저장한다.
        // 배치가 이 지점 이후 실패하더라도 재실행 시 항목을 다시 만들지 않고
        // 아래 markCalculated() 단계부터 재개할 수 있다.
        Settlement saved = settlementCommandRepositoryPort.saveWithItems(settlement, items);

        saved.markCalculated();

        return settlementCommandRepositoryPort.save(saved);
    }

    private Settlement resumeOrReturn(Settlement settlement) {
        if (settlement.getStatus() == SettlementStatus.CALCULATING) {
            settlement.markCalculated();
            return settlementCommandRepositoryPort.save(settlement);
        }

        if (settlement.getStatus() == SettlementStatus.CANCELLED) {
            throw SETTLEMENT_ALREADY_EXISTS.exception();
        }

        // CALCULATED 또는 CONFIRMED: 이미 계산이 끝난 정산이므로 재계산 없이 그대로 반환한다.
        return settlement;
    }

    @Override
    @Transactional
    public Settlement confirm(String settlementId) {
        Settlement settlement = settlementCommandRepositoryPort.findById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);

        settlement.confirm();

        return settlementCommandRepositoryPort.save(settlement);
    }

    @Override
    @Transactional
    public Settlement cancel(String settlementId) {
        Settlement settlement = settlementCommandRepositoryPort.findById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);

        settlement.cancel();

        return settlementCommandRepositoryPort.save(settlement);
    }
}