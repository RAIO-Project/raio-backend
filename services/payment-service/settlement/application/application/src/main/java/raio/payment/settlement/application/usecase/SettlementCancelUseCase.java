package raio.payment.settlement.application.usecase;

import raio.payment.settlement.domain.Settlement;

public interface SettlementCancelUseCase {

    Settlement cancel(String settlementId);
}