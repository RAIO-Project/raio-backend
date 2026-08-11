package raio.wallet.application.port;

import raio.wallet.domain.PointHistory;

public interface PointHistoryCommandRepositoryPort {
    
    PointHistory save(PointHistory history);
}
