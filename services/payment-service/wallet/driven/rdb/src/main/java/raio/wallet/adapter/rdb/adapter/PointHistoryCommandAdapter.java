package raio.wallet.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.wallet.application.port.PointHistoryCommandRepositoryPort;
import raio.wallet.domain.PointHistory;
import raio.wallet.adapter.rdb.mapper.PointHistoryEntityMapper;
import raio.wallet.adapter.rdb.repository.PointHistoryJpaRepository;

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
