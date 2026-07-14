package raio.stream.application.usecase;

import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 시작 (READY → LIVE). 방송 주인만 시작할 수 있다. */
public interface StreamStartUseCase {

    /**
     * @param requesterId 인증된 요청자(토큰에서 추출).
     */
    StreamDetail start(String streamId, String requesterId);
}
