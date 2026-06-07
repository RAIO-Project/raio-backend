package raio.payment.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PgProviderEntityTypeConverter
        implements AttributeConverter<PgProviderEntityType, Short> {

    @Override
    public Short convertToDatabaseColumn(PgProviderEntityType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public PgProviderEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : PgProviderEntityType.fromCode(dbData);
    }
}
