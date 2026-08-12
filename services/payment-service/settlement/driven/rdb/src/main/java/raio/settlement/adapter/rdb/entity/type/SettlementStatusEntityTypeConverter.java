package raio.settlement.adapter.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SettlementStatusEntityTypeConverter
        implements AttributeConverter<SettlementStatusEntityType, Short> {

    @Override
    public Short convertToDatabaseColumn(SettlementStatusEntityType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SettlementStatusEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : SettlementStatusEntityType.fromCode(dbData);
    }
}
