package raio.payment;

import raio.payment.domain.type.PointHistoryType;

import java.time.Instant;

public final class PointHistoryReadModels {
    
    private PointHistoryReadModels() {
    }
    
    /**
     * 포인트 이력 단건 조회 모델
     */
    public record PointHistoryDetail(
            String id,
            String walletId,
            String userId,
            PointHistoryType type,
            Long amount,
            Long balanceSnapshot,
            Instant createdAt
    ) {
    }
    
    /**
     * 포인트 이력 목록 조회 모델
     */
    public record PointHistorySummary(
            String id,
            PointHistoryType type,
            Long amount,
            Long balanceSnapshot,
            Instant createdAt
    ) {
    }
    
    /**
     * 포인트 잔액 + 최근 이력 조회 모델
     */
    public record WalletPointHistory(
            String walletId,
            String userId,
            Long balance,
            PointHistorySummary latestHistory
    ) {
    }
}