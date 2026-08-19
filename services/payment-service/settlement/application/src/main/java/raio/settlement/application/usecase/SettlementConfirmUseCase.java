package raio.settlement.application.usecase;

import raio.settlement.domain.Settlement;

public interface SettlementConfirmUseCase {

    Settlement confirm(String settlementId);
}