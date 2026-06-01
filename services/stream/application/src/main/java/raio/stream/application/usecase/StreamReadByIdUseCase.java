package raio.stream.application.usecase;

import raio.stream.readmodel.StreamQueryModels.StreamDetail;

/** 단건 상세 조회. 소켓 연결 전 LIVE 검증 + 메타 제공. */
public interface StreamReadByIdUseCase {
    /** 존재하지 않으면 STREAM_NOT_FOUND. */
    StreamDetail findById(String streamId);
}
