package raio.wallet.adapter.rdb.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistoryDetail;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistorySummary;
import raio.wallet.application.port.PointHistoryQueryRepositoryPort;
import raio.wallet.adapter.rdb.mapper.PointHistoryEntityMapper;
import raio.wallet.adapter.rdb.repository.PointHistoryJpaRepository;

import java.util.Optional;

import static raio.wallet.adapter.rdb.entity.QPointHistoryEntity.pointHistoryEntity;

@Repository
@RequiredArgsConstructor
public class PointHistoryQueryAdapter implements PointHistoryQueryRepositoryPort {
    
    private final PointHistoryJpaRepository pointHistoryJpaRepository;
    private final PointHistoryEntityMapper pointHistoryEntityMapper;
    private final JPAQueryFactory queryFactory;
    
    @Override
    public Optional<PointHistoryDetail> findPointHistoryDetailById(String id) {
        return pointHistoryJpaRepository.findById(Long.parseLong(id))
                .map(pointHistoryEntityMapper::toDetail);
    }
    
    @Override
    public Optional<PointHistoryDetail> findPointHistoryDetailByWalletId(String walletId) {
        return Optional.ofNullable(pointHistoryEntityMapper.toDetail(
                queryFactory.select(pointHistoryEntity)
                        .from(pointHistoryEntity)
                        .where(pointHistoryEntity.walletId.eq(Long.parseLong(walletId)))
                        .fetchOne()));
    }
    
    @Override
    public Page<PointHistorySummary> findPointHistorySummaryByWalletId(String walletId, Pageable pageable) {
        var content = queryFactory.select(pointHistoryEntity)
                .from(pointHistoryEntity)
                .where(pointHistoryEntity.walletId.eq(Long.parseLong(walletId)))
                .orderBy(pointHistoryEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(pointHistoryEntityMapper::toSummary)
                .toList();
        
        var countQuery = queryFactory.select(pointHistoryEntity.count())
                .from(pointHistoryEntity)
                .where(pointHistoryEntity.walletId.eq(Long.parseLong(walletId)));
        
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
