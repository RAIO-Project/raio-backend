package raio.stream.application.usecase;

import raio.stream.domain.type.StreamCategory;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 개설 (READY). 개설자는 인증된 요청자 본인이다. */
public interface StreamOpenUseCase {

    /**
     * @param streamerId 인증된 요청자(토큰에서 추출).
     */
    StreamDetail open(String streamerId, String title, StreamCategory category);
}
