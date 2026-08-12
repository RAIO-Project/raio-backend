package raio.settlement.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;
import raio.settlement.adapter.rdb.entity.SettlementSettingEntity;

@Mapper(componentModel = "spring")
public interface SettlementSettingEntityMapper {

    default SettlementSetting toDomain(SettlementSettingEntity entity) {
        if (entity == null) {
            return null;
        }

        return SettlementSetting.restore(
                String.valueOf(entity.streamerId),
                entity.currentCycle.toDomain(),
                entity.pendingCycle == null ? null : entity.pendingCycle.toDomain(),
                entity.pendingCycleEffectiveAt,
                entity.active,
                entity.createdAt,
                entity.updatedAt
        );
    }

    SettlementSettingEntity toEntity(SettlementSetting setting);

    SettlementSettingSummary toSummary(SettlementSettingEntity entity);
}
