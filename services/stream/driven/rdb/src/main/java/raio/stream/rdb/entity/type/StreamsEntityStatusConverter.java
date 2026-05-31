package raio.stream.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class StreamsEntityStatusConverter implements AttributeConverter<StreamsEntityStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StreamsEntityStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public StreamsEntityStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : StreamsEntityStatus.fromCode(code);
    }
}