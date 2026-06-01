package raio.payment.application.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.payment.PointHistoryReadModels.PointHistoryDetail;
import raio.payment.PointHistoryReadModels.PointHistorySummary;

import java.util.Optional;

public interface PointHistoryQueryRepositoryPort {
    
    Optional<PointHistoryDetail> findPointHistoryDetailById(String id);
    
    Optional<PointHistoryDetail> findPointHistoryDetailByWalletId(String walletId);
    
    Page<PointHistorySummary> findPointHistorySummaryByWalletId(String walletId, Pageable pageable);
}
