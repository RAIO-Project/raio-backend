package raio.settlement.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;

public interface SettlementReadUseCase {
    
    SettlementDetail getSettlement(String settlementId);

    Page<SettlementDetail> getSettlementsByStreamerId(String streamerId, Pageable pageable);
}
