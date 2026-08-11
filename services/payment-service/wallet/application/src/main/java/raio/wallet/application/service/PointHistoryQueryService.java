package raio.wallet.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistoryDetail;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistorySummary;
import raio.wallet.application.port.PointHistoryQueryRepositoryPort;
import raio.wallet.application.port.WalletCommandRepositoryPort;
import raio.wallet.application.usecase.PointHistoryReadUseCase;

import static raio.wallet.exception.WalletErrorCode.POINT_HISTORY_NOT_FOUND;
import static raio.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;


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
