package raio.settlement.adapter.rdb.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.application.port.SettlementSettingQueryRepositoryPort;
import raio.settlement.adapter.rdb.mapper.SettlementSettingEntityMapper;
import raio.settlement.adapter.rdb.repository.SettlementSettingJpaRepository;

import java.time.Instant;
import java.util.Optional;

import static raio.settlement.adapter.rdb.entity.QSettlementSettingEntity.settlementSettingEntity;

@Repository
@RequiredArgsConstructor
public class SettlementSettingQueryAdapter implements SettlementSettingQueryRepositoryPort {

    private final SettlementSettingJpaRepository settlementSettingJpaRepository;
    private final SettlementSettingEntityMapper settlementSettingEntityMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SettlementSettingSummary> findSettlementSettingByStreamerId(String streamerId) {
        return settlementSettingJpaRepository.findById(Long.parseLong(streamerId))
                .map(settlementSettingEntityMapper::toSummary);
    }
    
    @Override
    public Page<SettlementSettingSummary> findSettlementDueSettings(
            boolean active,
            Instant settlementStartAt,
            Pageable pageable
    ) {
        var condition = settlementSettingEntity.active.eq(active)
                .and(settlementSettingEntity.nextSettlementAt.loe(settlementStartAt));

        var content = queryFactory
                .select(settlementSettingEntity)
                .from(settlementSettingEntity)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(settlementSettingEntityMapper::toSummary)
                .toList();

        var countQuery = queryFactory
                .select(settlementSettingEntity.count())
                .from(settlementSettingEntity)
                .where(condition);

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}
