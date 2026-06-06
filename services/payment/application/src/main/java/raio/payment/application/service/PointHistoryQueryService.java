package raio.payment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import raio.payment.PointHistoryReadModels.PointHistoryDetail;
import raio.payment.PointHistoryReadModels.PointHistorySummary;
import raio.payment.application.port.PointHistoryQueryRepositoryPort;
import raio.payment.application.port.WalletCommandRepositoryPort;
import raio.payment.application.usecase.PointHistoryReadUseCase;

import static raio.payment.exception.PaymentErrorCode.POINT_HISTORY_NOT_FOUND;
import static raio.payment.exception.PaymentErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PointHistoryQueryService implements PointHistoryReadUseCase {
    
    private final WalletCommandRepositoryPort walletCommandRepository;
    private final PointHistoryQueryRepositoryPort pointHistoryQueryRepository;
    
    @Override
    public Page<PointHistorySummary> getPointHistorySummary(String walletId, Pageable pageable) {
        if (!walletCommandRepository.existsById(walletId)) {
            throw WALLET_NOT_FOUND.exception();
        }
        
        return pointHistoryQueryRepository.findPointHistorySummaryByWalletId(walletId, pageable);
    }
    
    @Override
    public PointHistoryDetail getPointHistoryDetail(String pointHistoryId) {
        return pointHistoryQueryRepository.findPointHistoryDetailById(pointHistoryId)
                .orElseThrow(POINT_HISTORY_NOT_FOUND::exception);
    }
}
