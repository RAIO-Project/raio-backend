package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.stream.application.port.StreamCommandPort;
import raio.stream.application.port.StreamLiveRankCommandPort;
import raio.stream.application.port.StreamLiveRankQueryPort;
import raio.stream.application.usecase.StreamEndUseCase;
import raio.stream.application.usecase.StreamOpenUseCase;
import raio.stream.application.usecase.StreamStartUseCase;
import raio.stream.domain.Streams;
import raio.stream.domain.type.StreamCategory;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;

import static raio.stream.exception.StreamErrorCode.STREAM_FORBIDDEN;

/**
 * <p>세 동작이 동일 포트({@link StreamCommandPort}, {@link StreamLiveRankCommandPort})를 공유하고
 * 전이(READY → LIVE → ENDED)가 하나의 흐름이므로 한 서비스로 묶는다.
 *
 * <p>방송의 상태 전이는 방송 주인만 수행할 수 있다. 요청자는 인증된 사용자(토큰)에서 오며,
 * 요청 본문으로 받지 않는다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StreamLifecycleCommandService implements
        StreamOpenUseCase,
        StreamStartUseCase,
        StreamEndUseCase {

    private final StreamCommandPort streamCommandPort;
    private final StreamLiveRankCommandPort streamLiveRankCommandPort;
    private final StreamLiveRankQueryPort streamLiveRankQueryPort;

    @Override
    public StreamDetail open(String streamerId, String title, StreamCategory category) {
        Streams saved = streamCommandPort.save(Streams.create(streamerId, title, category));
        return toDetail(saved);
    }

    @Override
    public StreamDetail start(String streamId, String requesterId) {
        Streams stream = streamCommandPort.getById(streamId);
        verifyOwner(stream, requesterId);

        stream.start(Instant.now());
        Streams updated = streamCommandPort.update(stream);

        streamLiveRankCommandPort.addLiveStream(Long.parseLong(updated.getId()));

        return toDetail(updated);
    }

    @Override
    public StreamDetail end(String streamId, String requesterId) {
        Streams stream = streamCommandPort.getById(streamId);
        verifyOwner(stream, requesterId);

        long streamIdLong = Long.parseLong(stream.getId());
        long finalViewers = streamLiveRankQueryPort.currentViewerCount(streamIdLong);

        stream.end(Instant.now(), (int) finalViewers);
        Streams updated = streamCommandPort.update(stream);

        streamLiveRankCommandPort.removeLiveStream(streamIdLong);

        return toDetail(updated);
    }

    /** 방송 주인만 상태를 바꿀 수 있다. */
    private void verifyOwner(Streams stream, String requesterId) {
        if (!stream.getStreamerId().equals(requesterId)) {
            throw STREAM_FORBIDDEN.exception();
        }
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