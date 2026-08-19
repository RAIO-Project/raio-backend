package raio.wallet.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.wallet.application.port.PointHistoryCommandRepositoryPort;
import raio.wallet.domain.PointHistory;
import raio.wallet.domain.type.PointHistoryType;
import raio.wallet.adapter.rdb.entity.type.PointHistoryEntityType;
import raio.wallet.adapter.rdb.mapper.PointHistoryEntityMapper;
import raio.wallet.adapter.rdb.repository.PointHistoryJpaRepository;

import java.util.Optional;

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

    @Override
    public Optional<PointHistory> findByWalletIdAndTypeAndSourceId(String walletId, PointHistoryType type, String sourceId) {
        return pointHistoryJpaRepository.findByWalletIdAndTypeAndSourceId(
                        Long.parseLong(walletId),
                        PointHistoryEntityType.valueOf(type),
                        Long.parseLong(sourceId)
                )
                .map(pointHistoryEntityMapper::toDomain);
    }
}
