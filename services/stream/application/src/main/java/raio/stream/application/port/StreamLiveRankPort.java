package raio.stream.application.port;

import java.util.List;

/**
 * 실시간 시청자 수 기준 라이브 스트림 랭킹 포트 (driven). Redis 어댑터가 구현.
 *
 * <p>동시 라이브 스트림 수는 절대량이 작다는 전제이므로 조회는 전체 랭킹을 순서대로 반환하고,
 * 페이징/필터는 상위 계층에서 적용한다.
 */
public interface StreamLiveRankPort {

    /** 라이브 스트림 한 건의 랭킹 정보. */
    record LiveRank(long streamId, long viewerCount) {
    }

    /** 시청자 수 내림차순 라이브 랭킹 전체 (동점은 ID 내림차순). */
    List<LiveRank> findLiveRanksByViewerDesc();

    /** 현재 시청자 수 조회 (없으면 0). 종료 시 최종값 동기화에 사용. */
    long currentViewerCount(long streamId);

    /** LIVE 전이 시 랭킹 편입 (기존 score 보존). */
    void addLiveStream(long streamId);

    /** ENDED 전이 시 랭킹에서 제거. */
    void removeLiveStream(long streamId);

    /** 시청자 입장. 갱신 후 현재 시청자 수 반환. */
    long increaseViewerCount(long streamId);

    /** 시청자 퇴장. 갱신 후 현재 시청자 수 반환. */
    long decreaseViewerCount(long streamId);
}
