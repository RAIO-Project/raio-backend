package raio.stream.application.usecase;

import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 종료 (LIVE → ENDED). Redis 랭킹에서 제거되고 최종 시청자 수가 동기화된다. */
public interface StreamEndUseCase {
    StreamDetail end(String streamId);
}
