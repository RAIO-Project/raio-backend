package raio.chat.application.port;

import java.util.Set;

/**
 * 실시간 시청자 수 추적 아웃바운드 포트.
 */
public interface ViewerPort {

    /** 시청자 입장 (세션을 방송 시청자 집합에 추가). 비로그인 포함 — 전체 연결 기준. */
    void join(Long streamId, String sessionId);

    /** 시청자 퇴장 (연결 종료). 세션이 보던 방송에서 제거. */
    void leave(String sessionId);

    /** 현재 시청자 수. */
    long count(Long streamId);

    /** 시청자가 있는(활성) 방송 ID 목록 — 스케줄러가 순회용. */
    Set<Long> activeStreamIds();
}