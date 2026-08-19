package raio.settlement.adapter.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SettlementCycleEntityTypeConverter
        implements AttributeConverter<SettlementCycleEntityType, Short> {

    @Override
    public Short convertToDatabaseColumn(SettlementCycleEntityType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SettlementCycleEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : SettlementCycleEntityType.fromCode(dbData);
    }
}
