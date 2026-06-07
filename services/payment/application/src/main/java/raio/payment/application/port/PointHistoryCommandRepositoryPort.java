package raio.payment.application.port;

import raio.payment.domain.PointHistory;

public interface PointHistoryCommandRepositoryPort {
    
    PointHistory save(PointHistory history);
}
