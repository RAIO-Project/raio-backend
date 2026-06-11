package raio.payment.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodEntityTypeConverter
        implements AttributeConverter<PaymentMethodEntityType, Short> {

    @Override
    public Short convertToDatabaseColumn(PaymentMethodEntityType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public PaymentMethodEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : PaymentMethodEntityType.fromCode(dbData);
    }
}
