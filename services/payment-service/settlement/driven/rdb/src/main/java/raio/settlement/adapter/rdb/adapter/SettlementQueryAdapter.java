package raio.settlement.adapter.rdb.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;
import raio.settlement.application.port.SettlementQueryRepositoryPort;
import raio.settlement.adapter.rdb.mapper.SettlementEntityMapper;
import raio.settlement.adapter.rdb.repository.SettlementJpaRepository;

import java.util.Optional;

import static raio.settlement.adapter.rdb.entity.QSettlementEntity.settlementEntity;

@Repository
@RequiredArgsConstructor
public class SettlementQueryAdapter implements SettlementQueryRepositoryPort {

    private final SettlementJpaRepository settlementJpaRepository;
    private final SettlementEntityMapper settlementEntityMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SettlementDetail> findSettlementById(String id) {
        return settlementJpaRepository.findById(id)
                .map(settlementEntityMapper::toDetail);
    }

    @Override
    public Page<SettlementDetail> findSettlementsByStreamerId(String streamerId, Pageable pageable) {
        Long streamerIdValue = Long.parseLong(streamerId);

        var content = queryFactory.select(settlementEntity)
                .from(settlementEntity)
                .where(settlementEntity.streamerId.eq(streamerIdValue))
                .orderBy(settlementEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(settlementEntityMapper::toDetail)
                .toList();

        var countQuery = queryFactory.select(settlementEntity.count())
                .from(settlementEntity)
                .where(settlementEntity.streamerId.eq(streamerIdValue));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
