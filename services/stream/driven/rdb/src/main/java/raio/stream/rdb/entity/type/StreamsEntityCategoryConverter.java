package raio.stream.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StreamsEntityCategoryConverter implements AttributeConverter<StreamsEntityCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StreamsEntityCategory category) {
        return category == null ? null : category.getCode();
    }

    @Override
    public StreamsEntityCategory convertToEntityAttribute(Integer code) {
        return code == null ? null : StreamsEntityCategory.fromCode(code);
    }
}