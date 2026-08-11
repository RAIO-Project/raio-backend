package raio.payment.settlement.application.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.payment.settlement.application.readmodel.SettlementReadModels.SettlementReadModel;

import java.util.Optional;

public interface SettlementQueryRepositoryPort {

    Optional<SettlementReadModel> findSettlementById(String id);

    Page<SettlementReadModel> findSettlementsByStreamerId(String streamerId, Pageable pageable);
}