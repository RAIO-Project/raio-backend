package raio.stream.application.usecase;

/** 실시간 시청자 수 조회. */
public interface StreamViewerReadUseCase {

    /** 현재 시청자 수 (집계에 영향 없음). */
    long currentCount(String streamId);
}
