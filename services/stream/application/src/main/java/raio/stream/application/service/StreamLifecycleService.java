package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.stream.application.port.StreamCommandPort;
import raio.stream.application.port.StreamLiveRankPort;
import raio.stream.application.usecase.StreamEndUseCase;
import raio.stream.application.usecase.StreamOpenUseCase;
import raio.stream.application.usecase.StreamStartUseCase;
import raio.stream.domain.Streams;
import raio.stream.domain.type.StreamCategory;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;

/**
 * <p>세 동작이 동일 포트({@link StreamCommandPort}, {@link StreamLiveRankPort})를 공유하고
 * 전이(READY → LIVE → ENDED)가 하나의 흐름이므로 한 서비스로 묶는다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StreamLifecycleService implements
        StreamOpenUseCase,
        StreamStartUseCase,
        StreamEndUseCase {

    private final StreamCommandPort streamCommandPort;
    private final StreamLiveRankPort streamLiveRankPort;

    @Override
    public StreamDetail open(String streamerId, String title, StreamCategory category) {
        Streams saved = streamCommandPort.save(Streams.create(streamerId, title, category));
        return toDetail(saved);
    }

    @Override
    public StreamDetail start(String streamId) {
        Streams stream = streamCommandPort.getById(streamId);
        stream.start(Instant.now());
        Streams updated = streamCommandPort.update(stream);

        streamLiveRankPort.addLiveStream(Long.parseLong(updated.getId()));

        return toDetail(updated);
    }

    @Override
    public StreamDetail end(String streamId) {
        Streams stream = streamCommandPort.getById(streamId);

        long streamIdLong = Long.parseLong(stream.getId());
        long finalViewers = streamLiveRankPort.currentViewerCount(streamIdLong);

        stream.end(Instant.now(), (int) finalViewers);
        Streams updated = streamCommandPort.update(stream);

        streamLiveRankPort.removeLiveStream(streamIdLong);

        return toDetail(updated);
    }

    private StreamDetail toDetail(Streams s) {
        return StreamDetail.builder()
                .id(s.getId())
                .streamerId(s.getStreamerId())
                .title(s.getTitle())
                .category(s.getCategory())
                .status(s.getStatus())
                .startedAt(s.getStartedAt())
                .build();
    }
}