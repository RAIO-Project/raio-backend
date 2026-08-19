package raio.stream.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import raio.stream.application.port.StreamLiveRankQueryPort;
import raio.stream.application.usecase.StreamViewerReadUseCase;

/** 실시간 시청자 수 조회. 값은 Redis 라이브 랭킹이 보관한다. */
@Service
@RequiredArgsConstructor
public class StreamViewerQueryService implements StreamViewerReadUseCase {

    private final StreamLiveRankQueryPort streamLiveRankQueryPort;

    @Override
    public long currentCount(String streamId) {
        return streamLiveRankQueryPort.currentViewerCount(Long.parseLong(streamId));
    }
}
