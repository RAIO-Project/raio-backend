package raio.stream.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.stream.application.port.StreamCommandPort;
import raio.stream.domain.Streams;
import raio.stream.rdb.entity.StreamsEntity;
import raio.stream.rdb.mapper.StreamsEntityMapper;
import raio.stream.rdb.repository.StreamsJpaRepository;

import static raio.stream.exception.StreamErrorCode.STREAM_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class StreamCommandAdapter implements StreamCommandPort {

    private final StreamsJpaRepository streamsJpaRepository;
    private final StreamsEntityMapper streamsEntityMapper;

    @Override
    public Streams save(Streams stream) {
        StreamsEntity saved = streamsJpaRepository.save(streamsEntityMapper.toEntity(stream));
        return streamsEntityMapper.toDomain(saved);
    }

    @Override
    public Streams getById(String id) {
        StreamsEntity entity = streamsJpaRepository.findById(Long.parseLong(id))
                .orElseThrow(STREAM_NOT_FOUND::exception);
        return streamsEntityMapper.toDomain(entity);
    }

    @Override
    public Streams update(Streams stream) {
        StreamsEntity entity = streamsJpaRepository.findById(Long.parseLong(stream.getId()))
                .orElseThrow(STREAM_NOT_FOUND::exception);

        // 상태 전이 결과 반영 (DynamicUpdate 라 변경 컬럼만 UPDATE)
        streamsEntityMapper.applyDomain(stream, entity);
        StreamsEntity saved = streamsJpaRepository.save(entity);
        return streamsEntityMapper.toDomain(saved);
    }
}
