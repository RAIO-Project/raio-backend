package raio.stream.application.usecase;

import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 종료 (LIVE → ENDED). 방송 주인만 종료할 수 있다. Redis 랭킹에서 제거되고 최종 시청자 수가 동기화된다. */
public interface StreamEndUseCase {

    /**
     * @param requesterId 인증된 요청자(토큰에서 추출). 방송 주인이 아니면 거부된다.
     */
    StreamDetail end(String streamId, String requesterId);
}
