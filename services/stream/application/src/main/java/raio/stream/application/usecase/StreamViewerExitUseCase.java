package raio.stream.application.usecase;

/** 시청자 퇴장 집계. 집계된 시청자의 연결이 끊겼을 때 호출된다. */
public interface StreamViewerExitUseCase {

    /** 시청자 퇴장. 갱신 후 현재 시청자 수 반환. */
    long exit(String streamId);
}
