package raio.payment.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PointHistoryEntityTypeConverter
        implements AttributeConverter<PointHistoryEntityType, Short> {
    
    @Override
    public Short convertToDatabaseColumn(PointHistoryEntityType attribute) {
        return attribute == null
                ? null
                : attribute.getCode();
    }
    
    @Override
    public PointHistoryEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null
                ? null
                : PointHistoryEntityType.fromCode(dbData);
    }
}