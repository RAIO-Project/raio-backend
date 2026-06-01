package raio.stream.application.usecase;

import raio.stream.domain.type.StreamCategory;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 방송 개설 (READY 상태로 생성). */
public interface StreamOpenUseCase {
    StreamDetail open(String streamerId, String title, StreamCategory category);
}
