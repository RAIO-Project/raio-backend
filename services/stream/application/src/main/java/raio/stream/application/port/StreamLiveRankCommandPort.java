package raio.stream.application.port;

/** 라이브 스트림 랭킹 갱신 포트. */
public interface StreamLiveRankCommandPort {

    /** LIVE 전이 시 랭킹 편입 (기존 score 보존). */
    void addLiveStream(long streamId);

    /** ENDED 전이 시 랭킹에서 제거. */
    void removeLiveStream(long streamId);

    /** 시청자 입장. 갱신 후 현재 시청자 수 반환. */
    long increaseViewerCount(long streamId);

    /** 시청자 퇴장. 갱신 후 현재 시청자 수 반환. */
    long decreaseViewerCount(long streamId);
}
