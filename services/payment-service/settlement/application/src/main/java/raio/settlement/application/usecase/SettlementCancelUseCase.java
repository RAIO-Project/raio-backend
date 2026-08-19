package raio.settlement.application.usecase;

import raio.settlement.domain.Settlement;

public interface SettlementCancelUseCase {

    Settlement cancel(String settlementId);
}