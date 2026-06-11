package raio.payment.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusEntityTypeConverter
        implements AttributeConverter<PaymentStatusEntityType, Short> {

    @Override
    public Short convertToDatabaseColumn(PaymentStatusEntityType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public PaymentStatusEntityType convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : PaymentStatusEntityType.fromCode(dbData);
    }
}
