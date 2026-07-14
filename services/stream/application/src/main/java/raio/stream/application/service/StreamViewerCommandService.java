package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.stream.application.port.StreamCommandPort;
import raio.stream.application.port.StreamLiveRankCommandPort;
import raio.stream.application.port.StreamLiveRankQueryPort;
import raio.stream.application.usecase.StreamViewerEnterUseCase;
import raio.stream.application.usecase.StreamViewerExitUseCase;
import raio.stream.domain.Streams;
import raio.stream.domain.type.StreamStatus;

/**
 * 시청자 수 집계. 카운트는 {@link StreamLiveRankCommandPort}(Redis 랭킹)가 보관하며,
 * 방송 종료 시 {@link StreamLifecycleCommandService#end} 가 이 값을 최종 시청자 수로 확정한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreamViewerCommandService implements StreamViewerEnterUseCase, StreamViewerExitUseCase {

    private final StreamCommandPort streamCommandPort;
    private final StreamLiveRankCommandPort streamLiveRankCommandPort;
    private final StreamLiveRankQueryPort streamLiveRankQueryPort;

    @Override
    public ViewerEnter enter(String streamId, String viewerId) {
        long id = Long.parseLong(streamId);
        Streams stream = streamCommandPort.getById(streamId);

        if (stream.getStatus() != StreamStatus.LIVE) {
            return new ViewerEnter(false, streamLiveRankQueryPort.currentViewerCount(id));
        }

        if (viewerId != null && viewerId.equals(stream.getStreamerId())) {
            log.debug("스트리머 본인 - 시청자 집계 제외 (streamId: {})", streamId);
            return new ViewerEnter(false, streamLiveRankQueryPort.currentViewerCount(id));
        }

        return new ViewerEnter(true, streamLiveRankCommandPort.increaseViewerCount(id));
    }

    @Override
    public long exit(String streamId) {
        return streamLiveRankCommandPort.decreaseViewerCount(Long.parseLong(streamId));
    }
}
