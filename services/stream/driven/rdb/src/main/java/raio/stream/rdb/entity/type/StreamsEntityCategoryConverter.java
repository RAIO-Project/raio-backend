package raio.stream.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StreamsEntityCategoryConverter implements AttributeConverter<StreamsEntityCategory, String> {

    @Override
    public String convertToDatabaseColumn(StreamsEntityCategory category) {
        return category == null ? null : category.name();
    }

    @Override
    public StreamsEntityCategory convertToEntityAttribute(String value) {
        return value == null ? null : StreamsEntityCategory.valueOf(value);
    }
}
