package raio.stream.application.usecase;

/**
 * 시청자 입장 집계. 방 구독 시점에 호출된다.
 *
 * <p>누구를 시청자로 셀지(스트리머 본인 제외, LIVE 상태에서만 집계)는 방송의 규칙이므로 여기서 판단하고,
 * 호출자는 집계 여부({@link ViewerEnter#counted()})만 확인하면 된다.
 */
public interface StreamViewerEnterUseCase {

    /**
     * 입장 처리 결과.
     *
     * @param counted     집계에 포함됐는지. false 면 퇴장 시 {@link StreamViewerExitUseCase} 를 호출하지 않는다.
     * @param viewerCount 갱신 후 현재 시청자 수
     */
    record ViewerEnter(boolean counted, long viewerCount) {
    }

    /**
     * 시청자 입장. 스트리머 본인이거나 LIVE 가 아니면 집계하지 않는다.
     *
     * @param viewerId 로그인 유저 ID. 비로그인(익명)이면 null
     */
    ViewerEnter enter(String streamId, String viewerId);
}
