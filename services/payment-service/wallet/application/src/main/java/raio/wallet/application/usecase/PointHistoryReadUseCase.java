package raio.wallet.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistoryDetail;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistorySummary;

public interface PointHistoryReadUseCase {
    
    Page<PointHistorySummary> getPointHistorySummary(String walletId, Pageable pageable);
    
    PointHistoryDetail getPointHistoryDetail(String pointHistoryId);
}
