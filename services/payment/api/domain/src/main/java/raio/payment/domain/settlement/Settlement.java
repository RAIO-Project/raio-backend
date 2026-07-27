package raio.payment.domain.settlement;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import raio.payment.domain.settlement.type.SettlementCycle;
import raio.payment.domain.settlement.type.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 실제 정산에 적용된 주기, 수수료율, 정산 기간의 과거 스냅샷.
 *
 * 앞으로 적용할 설정은 {@link SettlementSetting}이 담당하고,
 * Settlement는 특정 시점에 실제 적용된 정산 결과를 보존한다.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Settlement {
    
    /**
     * 정산 식별자.
     */
    @EqualsAndHashCode.Include
    private final String id;
    
    /**
     * 정산 대상 스트리머 식별자.
     */
    private final String streamerId;
    
    /**
     * 실제 정산에 적용된 정산 주기.
     */
    private final SettlementCycle cycle;
    
    /**
     * 정산 대상 기간 시작 시각.
     * 해당 시각은 정산 대상에 포함된다.
     */
    private final Instant periodStartAt;
    
    /**
     * 정산 대상 기간 종료 시각.
     * 해당 시각은 정산 대상에 포함되지 않는다.
     */
    private final Instant periodEndAt;
    
    /**
     * 수수료 차감 전 총 후원금.
     */
    private final BigDecimal grossAmount;
    
    /**
     * 실제 정산에 적용된 수수료율.
     */
    private final BigDecimal appliedFeeRate;
    
    /**
     * 플랫폼 수수료 총액.
     */
    private final BigDecimal feeAmount;
    
    /**
     * 스트리머의 최종 정산 대상 금액.
     */
    private final BigDecimal netAmount;
    
    /**
     * 현재 정산 처리 상태.
     */
    private SettlementStatus status;
    
    /**
     * 정산 생성 시각.
     */
    private final Instant createdAt;
    
    @Builder(access = AccessLevel.PRIVATE)
    private Settlement(
            String id,
            String streamerId,
            SettlementCycle cycle,
            Instant periodStartAt,
            Instant periodEndAt,
            BigDecimal grossAmount,
            BigDecimal appliedFeeRate,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            SettlementStatus status,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.streamerId = Objects.requireNonNull(streamerId, "streamerId");
        this.cycle = Objects.requireNonNull(cycle, "cycle");
        this.periodStartAt = Objects.requireNonNull(periodStartAt, "periodStartAt");
        this.periodEndAt = Objects.requireNonNull(periodEndAt, "periodEndAt");
        this.grossAmount = Objects.requireNonNull(grossAmount, "grossAmount");
        this.appliedFeeRate = Objects.requireNonNull(appliedFeeRate, "appliedFeeRate");
        this.feeAmount = Objects.requireNonNull(feeAmount, "feeAmount");
        this.netAmount = Objects.requireNonNull(netAmount, "netAmount");
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        
        validate();
    }
    
    /**
     * 정산 항목을 집계하여 신규 정산을 생성한다.
     */
    public static Settlement calculate(
            String id,
            String streamerId,
            SettlementCycle cycle,
            Instant periodStartAt,
            Instant periodEndAt,
            BigDecimal appliedFeeRate,
            List<SettlementItem> items,
            Instant createdAt
    ) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "정산 항목은 한 건 이상이어야 합니다."
            );
        }
        
        Set<String> donationIds = new HashSet<>();
        BigDecimal grossAmount = BigDecimal.ZERO;
        BigDecimal feeAmount = BigDecimal.ZERO;
        
        for (SettlementItem item : items) {
            Objects.requireNonNull(item, "settlementItem");
            
            if (!item.occurredWithin(periodStartAt, periodEndAt)) {
                throw new IllegalArgumentException(
                        "정산 기간을 벗어난 항목입니다: "
                                + item.getDonationId()
                );
            }
            
            if (item.getAppliedFeeRate().compareTo(appliedFeeRate) != 0) {
                throw new IllegalArgumentException(
                        "정산 수수료율이 일치하지 않습니다: "
                                + item.getDonationId()
                );
            }
            
            if (!donationIds.add(item.getDonationId())) {
                throw new IllegalArgumentException(
                        "중복된 후원 항목입니다: "
                                + item.getDonationId()
                );
            }
            
            grossAmount = grossAmount.add(item.getGrossAmount());
            feeAmount = feeAmount.add(item.getFeeAmount());
        }
        
        return Settlement.builder()
                .id(id)
                .streamerId(streamerId)
                .cycle(cycle)
                .periodStartAt(periodStartAt)
                .periodEndAt(periodEndAt)
                .grossAmount(grossAmount)
                .appliedFeeRate(appliedFeeRate)
                .feeAmount(feeAmount)
                .netAmount(grossAmount.subtract(feeAmount))
                .status(SettlementStatus.CALCULATING)
                .createdAt(createdAt)
                .build();
    }
    
    /**
     * 정산 집계를 완료한다.
     */
    public void markCalculated() {
        this.status = this.status.transitionTo(SettlementStatus.CALCULATED);
    }
    
    /**
     * 정산 결과를 최종 확정한다.
     */
    public void confirm() {
        this.status = this.status.transitionTo(SettlementStatus.CONFIRMED);
    }
    
    /**
     * 정산을 폐기한다.
     */
    public void cancel() {
        this.status = this.status.transitionTo(SettlementStatus.CANCELLED);
    }
    
    private void validate() {
        if (!periodStartAt.isBefore(periodEndAt)) {
            throw new IllegalArgumentException(
                    "정산 시작 시각은 종료 시각보다 이전이어야 합니다."
            );
        }
        
        if (grossAmount.signum() < 0
                || feeAmount.signum() < 0
                || netAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "정산 금액은 음수일 수 없습니다."
            );
        }
        
        if (appliedFeeRate.signum() < 0
                || appliedFeeRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "수수료율은 0 이상 1 이하여야 합니다."
            );
        }
        
        if (grossAmount.subtract(feeAmount).compareTo(netAmount) != 0) {
            throw new IllegalArgumentException(
                    "실정산액은 총액에서 수수료를 차감한 금액이어야 합니다."
            );
        }
    }
}