package raio.payment.domain.settlement;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * 하나의 후원 건이 특정 정산에 포함된 근거를 보존하는 정산 항목.
 * 원본 Donation의 식별자와 정산 당시 적용된 금액 및 수수료율을
 * 스냅샷으로 저장하여 중복 정산 방지와 정산 내역 추적에 사용한다.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SettlementItem {
    
    /**
     * 정산 항목 식별자.
     */
    @EqualsAndHashCode.Include
    private final String id;
    
    /**
     * 해당 항목이 포함된 부모 정산 식별자.
     */
    private final String settlementId;
    
    /**
     * 정산 대상이 된 원본 후원 식별자.
     * 동일한 후원 건이 여러 정산에 중복 포함되지 않도록
     * 중복 정산 방지 기준으로 사용한다.
     */
    private final String donationId;
    
    /**
     * 수수료 차감 전 후원 금액.
     */
    private final BigDecimal grossAmount;
    
    /**
     * 해당 후원 건에 실제 적용된 수수료율.
     * 예: 0.15는 15%를 의미한다.
     */
    private final BigDecimal appliedFeeRate;
    
    /**
     * 해당 후원 건에서 차감되는 플랫폼 수수료.
     * grossAmount × appliedFeeRate를 원 단위로 반올림한 금액이다.
     */
    private final BigDecimal feeAmount;
    
    /**
     * 스트리머에게 정산되는 최종 금액.
     * grossAmount - feeAmount와 같다.
     */
    private final BigDecimal netAmount;
    
    /**
     * 원본 후원 수익이 발생한 시각.
     * 해당 항목이 정산 대상 기간에 포함되는지 판단할 때 사용한다.
     */
    private final Instant revenueOccurredAt;
    
    @Builder(access = AccessLevel.PRIVATE)
    private SettlementItem(
            String id,
            String settlementId,
            String donationId,
            BigDecimal grossAmount,
            BigDecimal appliedFeeRate,
            BigDecimal feeAmount,
            BigDecimal netAmount,
            Instant revenueOccurredAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.settlementId = Objects.requireNonNull(settlementId, "settlementId");
        this.donationId = Objects.requireNonNull(donationId, "donationId");
        this.grossAmount = Objects.requireNonNull(grossAmount, "grossAmount");
        this.appliedFeeRate = Objects.requireNonNull(appliedFeeRate, "appliedFeeRate");
        this.feeAmount = Objects.requireNonNull(feeAmount, "feeAmount");
        this.netAmount = Objects.requireNonNull(netAmount, "netAmount");
        this.revenueOccurredAt = Objects.requireNonNull(revenueOccurredAt, "revenueOccurredAt");
        
        validate();
    }
    
    /**
     * 원본 후원 정보를 기준으로 신규 정산 항목을 생성한다.
     */
    public static SettlementItem create(
            String id,
            String settlementId,
            String donationId,
            BigDecimal grossAmount,
            BigDecimal appliedFeeRate,
            Instant revenueOccurredAt
    ) {
        Objects.requireNonNull(grossAmount, "grossAmount");
        Objects.requireNonNull(appliedFeeRate, "appliedFeeRate");
        
        BigDecimal feeAmount = grossAmount
                .multiply(appliedFeeRate)
                .setScale(0, RoundingMode.HALF_UP);
        
        BigDecimal netAmount = grossAmount.subtract(feeAmount);
        
        return SettlementItem.builder()
                .id(id)
                .settlementId(settlementId)
                .donationId(donationId)
                .grossAmount(grossAmount)
                .appliedFeeRate(appliedFeeRate)
                .feeAmount(feeAmount)
                .netAmount(netAmount)
                .revenueOccurredAt(revenueOccurredAt)
                .build();
    }
    
    /**
     * 후원 발생 시각이 주어진 정산 기간에 포함되는지 확인한다.
     * 정산 기간은 시작 시각을 포함하고 종료 시각은 포함하지 않는 반개구간으로 처리한다.
     * periodStartAt <= revenueOccurredAt < periodEndAt
     */
    public boolean occurredWithin(
            Instant periodStartAt,
            Instant periodEndAt
    ) {
        Objects.requireNonNull(periodStartAt, "periodStartAt");
        Objects.requireNonNull(periodEndAt, "periodEndAt");
        
        return !revenueOccurredAt.isBefore(periodStartAt)
                && revenueOccurredAt.isBefore(periodEndAt);
    }
    
    /**
     * 정산 항목의 금액 및 수수료 불변식을 검증한다.
     */
    private void validate() {
        if (grossAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "정산 대상 금액은 0보다 커야 합니다: " + grossAmount
            );
        }
        
        if (appliedFeeRate.signum() < 0
                || appliedFeeRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "수수료율은 0 이상 1 이하여야 합니다: " + appliedFeeRate
            );
        }
        
        if (feeAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "수수료 금액은 음수일 수 없습니다: " + feeAmount
            );
        }
        
        if (netAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "최종 정산 금액은 음수일 수 없습니다: " + netAmount
            );
        }
        
        if (feeAmount.compareTo(grossAmount) > 0) {
            throw new IllegalArgumentException(
                    "수수료 금액은 정산 대상 금액을 초과할 수 없습니다."
            );
        }
        
        if (grossAmount.subtract(feeAmount).compareTo(netAmount) != 0) {
            throw new IllegalArgumentException(
                    "최종 정산 금액은 정산 대상 금액에서 수수료를 차감한 금액이어야 합니다."
            );
        }
    }
}