package raio.payment.application.port;

import raio.payment.domain.wallet.PointHistory;

public interface PointHistoryCommandRepositoryPort {
    
    PointHistory save(PointHistory history);
}
