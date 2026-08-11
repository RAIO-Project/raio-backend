package raio.payment.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementReadModel;
import raio.payment.settlement.application.port.SettlementQueryRepositoryPort;
import raio.payment.settlement.application.usecase.SettlementReadUseCase;

import static raio.payment.exception.PaymentErrorCode.SETTLEMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementQueryService implements SettlementReadUseCase {

    private final SettlementQueryRepositoryPort settlementQueryRepositoryPort;

    @Override
    public SettlementReadModel getSettlement(String settlementId) {
        return settlementQueryRepositoryPort.findSettlementById(settlementId)
                .orElseThrow(SETTLEMENT_NOT_FOUND::exception);
    }

    @Override
    public Page<SettlementReadModel> getSettlementsByStreamerId(String streamerId, Pageable pageable) {
        return settlementQueryRepositoryPort.findSettlementsByStreamerId(streamerId, pageable);
    }
}
