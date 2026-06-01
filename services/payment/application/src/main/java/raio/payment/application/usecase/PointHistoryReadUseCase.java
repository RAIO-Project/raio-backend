package raio.payment.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.payment.PointHistoryReadModels.PointHistoryDetail;
import raio.payment.PointHistoryReadModels.PointHistorySummary;

public interface PointHistoryReadUseCase {
    
    Page<PointHistorySummary> getPointHistorySummary(String walletId, Pageable pageable);
    
    PointHistoryDetail getPointHistoryDetail(String pointHistoryId);
}
