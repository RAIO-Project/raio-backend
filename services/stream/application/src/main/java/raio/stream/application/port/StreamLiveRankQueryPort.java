package raio.stream.application.port;

import java.util.List;

/**
 * 실시간 시청자 수 기준 라이브 스트림 랭킹 조회 포트 (driven).
 */
public interface StreamLiveRankQueryPort {

    /** 라이브 스트림 한 건의 랭킹 정보. */
    record LiveRank(long streamId, long viewerCount) {
    }

    /** 시청자 수 내림차순 라이브 랭킹 전체 (동점은 ID 내림차순). */
    List<LiveRank> findLiveRanksByViewerDesc();

    /** 현재 시청자 수 조회 (없으면 0). 종료 시 최종값 동기화에 사용. */
    long currentViewerCount(long streamId);
}
