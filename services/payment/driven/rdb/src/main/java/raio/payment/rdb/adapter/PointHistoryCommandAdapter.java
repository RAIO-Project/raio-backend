package raio.payment.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.payment.application.port.PointHistoryCommandRepositoryPort;
import raio.payment.domain.PointHistory;
import raio.payment.rdb.mapper.PointHistoryEntityMapper;
import raio.payment.rdb.repository.PointHistoryJpaRepository;

@Repository
@RequiredArgsConstructor
public class PointHistoryCommandAdapter implements PointHistoryCommandRepositoryPort {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;
    private final PointHistoryEntityMapper pointHistoryEntityMapper;

    @Override
    public PointHistory save(PointHistory history) {
        var saved = pointHistoryJpaRepository.save(pointHistoryEntityMapper.toEntity(history));
        pointHistoryJpaRepository.flush();
        return pointHistoryEntityMapper.toDomain(saved);
    }
}
