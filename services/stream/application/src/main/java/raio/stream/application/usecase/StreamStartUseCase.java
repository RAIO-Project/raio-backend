package raio.stream.application.usecase;

import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 시작 (READY → LIVE). Redis 라이브 랭킹에 편입된다. */
public interface StreamStartUseCase {
    StreamDetail start(String streamId);
}
