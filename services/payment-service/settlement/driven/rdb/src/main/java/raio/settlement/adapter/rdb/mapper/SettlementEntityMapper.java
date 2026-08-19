package raio.settlement.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.settlement.domain.Settlement;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;
import raio.settlement.adapter.rdb.entity.SettlementEntity;

@Mapper(componentModel = "spring")
public interface SettlementEntityMapper {

    default Settlement toDomain(SettlementEntity entity) {
        if (entity == null) {
            return null;
        }

        return Settlement.restore(
                entity.id,
                String.valueOf(entity.streamerId),
                entity.cycle.toDomain(),
                entity.periodStartAt,
                entity.periodEndAt,
                entity.grossAmount,
                entity.appliedFeeRate,
                entity.feeAmount,
                entity.netAmount,
                entity.status.toDomain(),
                entity.createdAt
        );
    }

    SettlementEntity toEntity(Settlement settlement);

    SettlementDetail toDetail(SettlementEntity entity);
}
