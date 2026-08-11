package raio.payment.settlement.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import raio.payment.settlement.domain.type.SettlementCycle;

import java.time.Instant;
import java.util.Objects;

/**
 * 스트리머에게 적용할 정산 설정.
 *
 * <p>정산 주기 변경은 즉시 적용하지 않고 다음 정산 기간부터 적용한다.
 * 현재 적용 중인 주기와 예약된 주기를 분리하여 관리한다.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SettlementSetting {
    
    /**
     * 정산 설정의 대상 스트리머 식별자.
     * 스트리머별로 하나의 정산 설정만 존재한다.
     */
    @EqualsAndHashCode.Include
    private final String streamerId;
    
    /**
     * 현재 적용 중인 정산 주기.
     */
    private SettlementCycle currentCycle;
    
    /**
     * 다음 정산 기간부터 적용할 예약 정산 주기.
     */
    private SettlementCycle pendingCycle;
    
    /**
     * 예약된 정산 주기가 적용되는 정산 기간 시작 시각.
     */
    private Instant pendingCycleEffectiveAt;
    
    /**
     * 정산 설정 활성 여부.
     */
    private boolean active;
    
    /**
     * 정산 설정 생성 시각.
     */
    private final Instant createdAt;
    
    /**
     * 정산 설정 최종 변경 시각.
     */
    private Instant updatedAt;
    
    private SettlementSetting(
            String streamerId,
            SettlementCycle currentCycle,
            SettlementCycle pendingCycle,
            Instant pendingCycleEffectiveAt,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.streamerId = streamerId;
        this.currentCycle = currentCycle;
        this.pendingCycle = pendingCycle;
        this.pendingCycleEffectiveAt = pendingCycleEffectiveAt;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * 스트리머의 정산 설정을 생성한다.
     */
    public static SettlementSetting create(
            String streamerId,
            SettlementCycle initialCycle,
            Instant now
    ) {
        requireText(streamerId);
        Objects.requireNonNull(initialCycle, "initialCycle");
        Objects.requireNonNull(now, "now");
        
        return new SettlementSetting(
                streamerId,
                initialCycle,
                null,
                null,
                true,
                now,
                now
        );
    }
    
    /**
     * 정산 주기 변경을 예약한다.
     *
     * <p>변경된 주기는 {@code effectiveAt}부터 시작하는 정산 기간에 적용된다.
     * 기존 예약이 있다면 새로운 예약으로 대체한다.</p>
     */
    public void requestCycleChange(
            SettlementCycle newCycle,
            Instant effectiveAt,
            Instant now
    ) {
        Objects.requireNonNull(newCycle, "newCycle");
        Objects.requireNonNull(effectiveAt, "effectiveAt");
        Objects.requireNonNull(now, "now");
        
        if (!active) {
            throw new IllegalStateException("비활성화된 정산 설정은 변경할 수 없습니다.");
        }
        
        if (!effectiveAt.isAfter(now)) {
            throw new IllegalArgumentException(
                    "정산 주기 적용 시각은 현재 시각 이후여야 합니다: " + effectiveAt
            );
        }
        
        if (newCycle == currentCycle) {
            throw new IllegalArgumentException(
                    "현재 정산 주기와 동일한 주기로 변경할 수 없습니다: " + newCycle
            );
        }
        
        this.pendingCycle = newCycle;
        this.pendingCycleEffectiveAt = effectiveAt;
        this.updatedAt = now;
    }
    
    /**
     * 해당 정산 기간에 적용할 정산 주기를 반환한다.
     */
    public SettlementCycle resolveCycleFor(Instant periodStartAt) {
        Objects.requireNonNull(periodStartAt, "periodStartAt");
        
        return isPendingCycleDue(periodStartAt)
                ? pendingCycle
                : currentCycle;
    }
    
    /**
     * 예약된 정산 주기의 적용 시점이 도래한 경우 현재 주기에 반영한다.
     */
    public void applyPendingCycleIfDue(
            Instant periodStartAt,
            Instant now
    ) {
        Objects.requireNonNull(periodStartAt, "periodStartAt");
        Objects.requireNonNull(now, "now");
        
        if (!isPendingCycleDue(periodStartAt)) {
            return;
        }
        
        this.currentCycle = pendingCycle;
        this.pendingCycle = null;
        this.pendingCycleEffectiveAt = null;
        this.updatedAt = now;
    }
    
    /**
     * 예약된 정산 주기 변경을 취소한다.
     */
    public void cancelPendingCycleChange(Instant now) {
        Objects.requireNonNull(now, "now");
        
        if (pendingCycle == null) {
            return;
        }
        
        this.pendingCycle = null;
        this.pendingCycleEffectiveAt = null;
        this.updatedAt = now;
    }
    
    /**
     * 정산 설정을 활성화한다.
     */
    public void activate(Instant now) {
        Objects.requireNonNull(now, "now");
        
        if (active) {
            return;
        }
        
        this.active = true;
        this.updatedAt = now;
    }
    
    /**
     * 정산 설정을 비활성화한다.
     */
    public void deactivate(Instant now) {
        Objects.requireNonNull(now, "now");
        
        if (!active) {
            return;
        }
        
        this.active = false;
        this.updatedAt = now;
    }
    
    private boolean isPendingCycleDue(Instant periodStartAt) {
        return pendingCycle != null
                && pendingCycleEffectiveAt != null
                && !periodStartAt.isBefore(pendingCycleEffectiveAt);
    }
    
    private static void requireText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "streamerId must not be blank"
            );
        }
    }
}