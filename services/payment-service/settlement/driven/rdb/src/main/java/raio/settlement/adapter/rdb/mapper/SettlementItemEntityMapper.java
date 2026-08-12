package raio.settlement.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.settlement.domain.SettlementItem;
import raio.settlement.adapter.rdb.entity.SettlementItemEntity;

@Mapper(componentModel = "spring")
public interface SettlementItemEntityMapper {

    default SettlementItem toDomain(SettlementItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return SettlementItem.restore(
                entity.id,
                entity.settlementId,
                String.valueOf(entity.donationId),
                entity.grossAmount,
                entity.appliedFeeRate,
                entity.feeAmount,
                entity.netAmount,
                entity.revenueOccurredAt
        );
    }

    SettlementItemEntity toEntity(SettlementItem item);
}
