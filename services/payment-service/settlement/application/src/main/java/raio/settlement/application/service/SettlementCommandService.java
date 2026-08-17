package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.settlement.application.command.SettlementCommands.SettlementCalculateCommand;
import raio.settlement.application.port.SettlementCommandRepositoryPort;
import raio.settlement.application.port.SettlementDonationQueryPort;
import raio.settlement.application.port.SettlementSettingCommandRepositoryPort;
import raio.settlement.application.strategy.resolver.SettlementFeeResolver;
import raio.settlement.application.usecase.SettlementCalculateUseCase;
import raio.settlement.application.usecase.SettlementCancelUseCase;
import raio.settlement.application.usecase.SettlementConfirmUseCase;
import raio.settlement.domain.Settlement;
import raio.settlement.domain.SettlementItem;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.domain.policy.SettlementFeeContext;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementCommandService implements SettlementCalculateUseCase, SettlementConfirmUseCase, SettlementCancelUseCase {
    
    private final SettlementCommandRepositoryPort settlementCommandRepositoryPort;
    private final SettlementSettingCommandRepositoryPort settlementSettingCommandRepositoryPort;
    private final SettlementDonationQueryPort settlementDonationQueryPort;
    private final SettlementFeeResolver settlementFeeResolver;
    
    /**
     * 지정된 스트리머와 정산 기간을 기준으로 정산 금액을 계산한다.
     *
     * 처리 흐름:
     * 1. 동일 기간 정산 존재 여부 확인
     * 2. 스트리머 정산 설정 및 적용 주기 결정
     * 3. 미정산 후원 내역 조회
     * 4. 적용 수수료율 결정
     * 5. SettlementItem 생성 및 금액 집계
     * 6. Settlement 저장
     * 7. CALCULATING -> CALCULATED 상태 전이
     */
    @Override
    @Transactional
    public Settlement calculate(SettlementCalculateCommand command) {

        log.debug("[정산 계산 시작(CALCULATE)] streamerId={}, periodEndAt={}",
                command.streamerId(),
                command.periodEndAt()
        );

        // 스트리머별 정산 설정을 조회한다. 정산 대상 기간의 시작 시각은
        // 배치가 아니라 이 설정(마지막 정산 완료 시각)이 결정한다.
        var setting = settlementSettingCommandRepositoryPort.findByStreamerId(command.streamerId())
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);

        Instant periodStartAt = setting.getLastSettledAt() != null
                ? setting.getLastSettledAt()
                : setting.getCreatedAt();
        Instant periodEndAt = command.periodEndAt();

        // 동일 스트리머/기간에 생성된 정산이 있는지 확인한다.
        var existing = settlementCommandRepositoryPort.findByStreamerIdAndPeriod(command.streamerId(), periodStartAt, periodEndAt);

        if (existing.isPresent()) {
            log.warn("[기존 정산 발견(SETTLEMENT_EXISTS)] settlementId={}, streamerId={}, status={}, periodStartAt={}, periodEndAt={}",
                    existing.get().getId(),
                    command.streamerId(),
                    existing.get().getStatus(),
                    periodStartAt,
                    periodEndAt
            );

            Settlement resumed = resumeOrReturn(existing.get());
            advanceSetting(setting, periodEndAt);

            return resumed;
        }

        // 해당 정산 기간에 실제 적용할 정산 주기를 결정한다.
        SettlementCycle cycle = setting.resolveCycleFor(periodStartAt);

        log.debug("[정산 주기 결정(CYCLE_RESOLVED)] streamerId={}, cycle={}, periodStartAt={}",
                command.streamerId(),
                cycle,
                periodStartAt
        );

        // 대상 기간 내 아직 정산되지 않은 후원 내역을 조회한다.
        var donations = settlementDonationQueryPort.findUnsettledDonations(command.streamerId(), periodStartAt, periodEndAt);

        if (donations.isEmpty()) {
            log.debug("[정산 대상 없음(NO_SETTLEMENT_TARGET)] streamerId={}, periodStartAt={}, periodEndAt={}",
                    command.streamerId(),
                    periodStartAt,
                    periodEndAt
            );

            // 대상이 없어도 다음 정산 예정 시각은 전진시켜야 배치가 이 스트리머를
            // 매번 다시 정산 대상으로 선정하는 것을 막을 수 있다.
            advanceSetting(setting, periodEndAt);

            throw SETTLEMENT_TARGET_NOT_FOUND.exception();
        }

        log.debug("[정산 대상 조회(DONATION_TARGETS)] streamerId={}, donationCount={}",
                command.streamerId(),
                donations.size()
        );

        Instant now = Instant.now();

        // 정산 시점의 정책을 기준으로 실제 적용할 수수료율을 결정한다.
        BigDecimal feeRate = settlementFeeResolver.resolve(
                new SettlementFeeContext(
                        command.streamerId(),
                        cycle,
                        now
                )
        );

        log.debug("[정산 수수료 결정(FEE_RESOLVED)] streamerId={}, cycle={}, feeRate={}",
                command.streamerId(),
                cycle,
                feeRate
        );

        String settlementId = UUID.randomUUID().toString();

        // 각 Donation을 정산 당시의 금액/수수료율을 보존하는 SettlementItem으로 변환한다.
        List<SettlementItem> items = donations.stream()
                .map(donation -> SettlementItem.create(
                        UUID.randomUUID().toString(),
                        settlementId,
                        donation.donationId(),
                        donation.grossAmount(),
                        feeRate,
                        donation.occurredAt()
                ))
                .toList();

        // SettlementItem의 합계를 기준으로 Settlement 스냅샷을 생성한다.
        Settlement settlement = Settlement.calculate(
                settlementId,
                command.streamerId(),
                cycle,
                periodStartAt,
                periodEndAt,
                feeRate,
                items,
                now
        );

        log.debug(
                "[정산 생성(CALCULATING)] settlementId={}, streamerId={}, cycle={}, itemCount={}, grossAmount={}, feeAmount={}, netAmount={}",
                settlement.getId(),
                settlement.getStreamerId(),
                settlement.getCycle(),
                items.size(),
                settlement.getGrossAmount(),
                settlement.getFeeAmount(),
                settlement.getNetAmount()
        );

        /*
         * Settlement와 SettlementItem을 함께 저장한다.
         *
         * 현재 calculate() 전체가 하나의 Transaction이므로
         * 이후 처리 중 예외가 발생하면 이 저장 역시 함께 Rollback 된다.
         *
         * 향후 CALCULATING 상태를 실제 복구 지점으로 사용할 경우에는
         * CALCULATING 저장과 CALCULATED 전이를 별도 Transaction으로 분리해야 한다.
         */
        Settlement saved = settlementCommandRepositoryPort.saveWithItems(
                settlement,
                items
        );

        // 집계가 정상적으로 끝났으므로 CALCULATING -> CALCULATED로 전이한다.
        saved.markCalculated();

        Settlement calculated = settlementCommandRepositoryPort.save(saved);

        // 이번 정산 대상 기간 종료 시각을 기준으로 다음 정산 예정 시각을 전진시킨다.
        advanceSetting(setting, periodEndAt);

        log.info(
                "[정산 계산 완료(CALCULATED)] settlementId={}, streamerId={}, cycle={}, itemCount={}, grossAmount={}, feeAmount={}, netAmount={}",
                calculated.getId(),
                calculated.getStreamerId(),
                calculated.getCycle(),
                items.size(),
                calculated.getGrossAmount(),
                calculated.getFeeAmount(),
                calculated.getNetAmount()
        );

        return calculated;
    }

    /**
     * 정산 처리 결과와 무관하게, 이번 정산 대상 기간이 끝났음을 정산 설정에 반영한다.
     *
     * <p>calculate()가 정산 대상 없음(SETTLEMENT_TARGET_NOT_FOUND) 등으로 예외를 던지면
     * calculate() 전체를 감싼 트랜잭션이 롤백되므로, 이 갱신만은 별도 트랜잭션(REQUIRES_NEW)으로
     * 커밋해 배치가 동일 스트리머를 매번 다시 정산 대상으로 선정하는 것을 막는다.</p>
     */
    private void advanceSetting(SettlementSetting setting, Instant periodEndAt) {
        settlementSettingCommandRepositoryPort.transactionRequiresNew(() -> {
            setting.markSettled(periodEndAt, Instant.now());
            return settlementSettingCommandRepositoryPort.save(setting);
        });
    }
    
    /**
     * 동일 기간에 이미 존재하는 Settlement를 처리한다.
     *
     * CALCULATING : 계산 완료 상태로 전이
     * CALCULATED  : 기존 결과 반환
     * CONFIRMED   : 기존 결과 반환
     * CANCELLED   : 동일 기간 신규 정산 생성을 막고 예외 발생
     */
    private Settlement resumeOrReturn(Settlement settlement) {
        
        if (settlement.getStatus() == SettlementStatus.CALCULATING) {
            log.warn(
                    "[정산 계산 재개(RESUME_CALCULATING)] settlementId={}, streamerId={}",
                    settlement.getId(),
                    settlement.getStreamerId()
            );
            
            settlement.markCalculated();
            
            Settlement calculated =
                    settlementCommandRepositoryPort.save(settlement);
            
            log.info(
                    "[정산 계산 완료(CALCULATED)] settlementId={}, streamerId={}",
                    calculated.getId(),
                    calculated.getStreamerId()
            );
            
            return calculated;
        }
        
        if (settlement.getStatus() == SettlementStatus.CANCELLED) {
            log.warn(
                    "[취소된 정산 존재(CANCELLED_EXISTS)] settlementId={}, streamerId={}",
                    settlement.getId(),
                    settlement.getStreamerId()
            );
            
            throw SETTLEMENT_ALREADY_EXISTS.exception();
        }
        
        // CALCULATED 또는 CONFIRMED 상태는 이미 계산이 끝났으므로 재집계하지 않는다.
        log.debug(
                "[기존 정산 반환(RETURN_EXISTING)] settlementId={}, streamerId={}, status={}",
                settlement.getId(),
                settlement.getStreamerId(),
                settlement.getStatus()
        );
        
        return settlement;
    }
    
    /**
     * 계산이 완료된 정산을 최종 확정한다.
     *
     * CALCULATED -> CONFIRMED
     */
    @Override
    @Transactional
    public Settlement confirm(String settlementId) {
        
        log.debug(
                "[정산 확정 요청(CONFIRM)] settlementId={}",
                settlementId
        );
        
        Settlement settlement = settlementCommandRepositoryPort
                .findById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);
        
        settlement.confirm();
        
        Settlement confirmed =
                settlementCommandRepositoryPort.save(settlement);
        
        log.info(
                "[정산 확정 완료(CONFIRMED)] settlementId={}, streamerId={}, netAmount={}",
                confirmed.getId(),
                confirmed.getStreamerId(),
                confirmed.getNetAmount()
        );
        
        return confirmed;
    }
    
    /**
     * 계산 중이거나 계산 완료된 정산을 취소한다.
     *
     * CALCULATING / CALCULATED -> CANCELLED
     */
    @Override
    @Transactional
    public Settlement cancel(String settlementId) {
        
        log.debug(
                "[정산 취소 요청(CANCEL)] settlementId={}",
                settlementId
        );
        
        Settlement settlement = settlementCommandRepositoryPort
                .findById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);
        
        settlement.cancel();
        
        Settlement cancelled =
                settlementCommandRepositoryPort.save(settlement);
        
        log.info(
                "[정산 취소 완료(CANCELLED)] settlementId={}, streamerId={}",
                cancelled.getId(),
                cancelled.getStreamerId()
        );
        
        return cancelled;
    }
}