package raio.payment.settlement.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementReadModel;

public interface SettlementReadUseCase {

    SettlementReadModel getSettlement(String settlementId);

    Page<SettlementReadModel> getSettlementsByStreamerId(String streamerId, Pageable pageable);
}
