package raio.stream.rdb.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 샘플의 {@code BoardEntityStatusConverter} 패턴 적용.
 * 단, DB 컬럼이 VARCHAR(20) 이므로 Integer 가 아닌 String 으로 변환한다.
 */
@Converter
public class StreamsEntityStatusConverter implements AttributeConverter<StreamsEntityStatus, String> {

    @Override
    public String convertToDatabaseColumn(StreamsEntityStatus status) {
        return status == null ? null : status.name();
    }

    @Override
    public StreamsEntityStatus convertToEntityAttribute(String value) {
        return value == null ? null : StreamsEntityStatus.valueOf(value);
    }
}
