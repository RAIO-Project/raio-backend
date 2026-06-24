package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ViewerPort;

/**
 * 5초마다 활성 방송의 현재 시청자 수를 브로드캐스트.
 */
@Component
@RequiredArgsConstructor
public class ViewerCountScheduler {

    private final ViewerPort viewerPort;
    private final ChatBroadcastPort chatBroadcastPort;

    @Scheduled(fixedRate = 5000) // 5초
    public void broadcastViewerCounts() {
        for (Long streamId : viewerPort.activeStreamIds()) {
            long count = viewerPort.count(streamId);
            // TODO: Moderation Main 브랜치에 머지 후 주석 제거 및 ChatBroadcastPort, ChatBroadcastAdapter 메소드 추가
//            chatBroadcastPort.broadcastViewerCount(streamId, count);
        }
    }
}