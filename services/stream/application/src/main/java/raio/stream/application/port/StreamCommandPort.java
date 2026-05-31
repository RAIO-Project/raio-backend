package raio.stream.application.port;

import raio.stream.domain.Streams;

public interface StreamCommandPort {

    /** 신규 방송 저장. 저장된(ID 채번된) 도메인 반환. */
    Streams save(Streams stream);

    /** ID 로 방송 조회. 없으면 STREAM_NOT_FOUND. */
    Streams getById(String id);

    /** 상태/시각 등 변경 사항 반영. */
    Streams update(Streams stream);
}
