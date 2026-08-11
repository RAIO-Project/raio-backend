package raio.payment.settlement.application.usecase;

import raio.payment.settlement.domain.Settlement;

public interface SettlementConfirmUseCase {

    Settlement confirm(String settlementId);
}