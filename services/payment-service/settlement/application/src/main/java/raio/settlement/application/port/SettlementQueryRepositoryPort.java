package raio.settlement.application.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;

import java.util.Optional;

public interface SettlementQueryRepositoryPort {

    Optional<SettlementDetail> findSettlementById(String id);

    Page<SettlementDetail> findSettlementsByStreamerId(String streamerId, Pageable pageable);
}