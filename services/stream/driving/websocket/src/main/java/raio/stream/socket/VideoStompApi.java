package raio.stream.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import raio.socket.interceptor.StompPrincipal;
import raio.socket.relay.StreamRelayChannel;
import raio.stream.application.port.StreamQueryPort;
import raio.stream.socket.dto.VideoWebSocketDto.VideoSyncCommand;
import raio.stream.socket.relay.VideoMessage;

import java.security.Principal;
import java.time.Instant;

/**
 * 방장이 업로드한 영상의 URL·재생 상태를 구독 중인 모든 시청자에게 브로드캐스트.
 * 클라이언트 → /app/streams/{streamId}/video → Redis → /topic/streams/{streamId}
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class VideoStompApi {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final StreamQueryPort streamQueryPort;

    @MessageMapping("/streams/{streamId}/video")
    public void syncVideo(
            @DestinationVariable String streamId,
            @Payload VideoSyncCommand command,
            Principal principal) {

        if (!(principal instanceof StompPrincipal user)) {
            log.warn("[VideoSync] 인증 실패(익명) → 거부 streamId={}", streamId);
            return;
        }

        Long userId = Long.parseLong(user.getName());

        // 해당 방송의 방장인지 검증
        boolean isOwner = streamQueryPort.findDetailById(streamId)
                .map(detail -> String.valueOf(userId).equals(detail.streamerId()))
                .orElse(false);

        if (!isOwner) {
            log.warn("[VideoSync] 권한 없음 → 거부 userId={} streamId={}", userId, streamId);
            return;
        }

        VideoMessage message = new VideoMessage(
                "VIDEO",
                streamId,
                command.videoUrl(),
                command.currentTime(),
                command.playing(),
                Instant.now()
        );

        try {
            String json = objectMapper.writeValueAsString(message);
            stringRedisTemplate.convertAndSend(StreamRelayChannel.redisChannel(streamId), json);
            log.info("[VideoSync] 발행 완료 userId={} streamId={} playing={}", userId, streamId, command.playing());
        } catch (Exception e) {
            log.error("[VideoSync] Redis publish 실패 streamId={}", streamId, e);
        }
    }
}
