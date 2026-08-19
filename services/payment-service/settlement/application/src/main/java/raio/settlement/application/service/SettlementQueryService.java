package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;
import raio.settlement.application.port.SettlementQueryRepositoryPort;
import raio.settlement.application.usecase.SettlementReadUseCase;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementQueryService implements SettlementReadUseCase {

    private final SettlementQueryRepositoryPort settlementQueryRepositoryPort;

    @Override
    public SettlementDetail getSettlement(String settlementId) {
        return settlementQueryRepositoryPort.findSettlementById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);
    }

    @Override
    public Page<SettlementDetail> getSettlementsByStreamerId(String streamerId, Pageable pageable) {
        return settlementQueryRepositoryPort.findSettlementsByStreamerId(streamerId, pageable);
    }
}
