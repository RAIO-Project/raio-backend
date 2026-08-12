package raio.settlement.domain.type;

public enum SettlementStatus {
    
    /**
     * 정산 계산이 진행 중인 상태
     */
    CALCULATING,
    
    /**
     * 정산 금액 계산이 완료된 상태
     */
    CALCULATED,
    
    /**
     * 정산 결과가 최종 확정된 상태
     */
    CONFIRMED,
    
    /**
     * 정산이 취소되어 재정산이 필요한 상태
     */
    CANCELLED;

    // 상태 전이 규칙
    public boolean canTransitionTo(SettlementStatus next) {
        return switch (this) {
            case CALCULATING -> next == CALCULATED || next == CANCELLED;
            case CALCULATED -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED, CANCELLED -> false;
        };
    }
    
    public SettlementStatus transitionTo(SettlementStatus next){
        if (!canTransitionTo(next)) {
            throw new IllegalStateException(
                    this + " -> " + next + " 상태 변경은 허용되지 않습니다."
            );
        }

        return next;
    }
}
