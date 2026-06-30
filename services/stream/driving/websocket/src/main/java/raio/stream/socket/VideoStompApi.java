package raio.stream.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import raio.socket.interceptor.StompAuthChannelInterceptor;
import raio.socket.relay.StreamRelayChannel;
import raio.stream.socket.dto.VideoWebSocketDto.VideoSyncCommand;
import raio.stream.socket.relay.VideoMessage;

import java.time.Instant;
import java.util.Map;

/**
 * 방장이 업로드한 영상의 URL·재생 상태를 구독 중인 모든 시청자에게 브로드캐스트.
 * 클라이언트 → /app/streams/{streamId}/video → Redis → /topic/streams/{streamId}
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class VideoStompApi {

    private static final long ANONYMOUS_USER_ID = -1L;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/streams/{streamId}/video")
    public void syncVideo(
            @DestinationVariable String streamId,
            @Payload VideoSyncCommand command,
            @Header(SimpMessageHeaderAccessor.SESSION_ATTRIBUTES) Map<String, Object> sessionAttrs) {

        var userId = (sessionAttrs != null && sessionAttrs.get(StompAuthChannelInterceptor.USER_ID) != null)
                ? (Long) sessionAttrs.get(StompAuthChannelInterceptor.USER_ID)
                : ANONYMOUS_USER_ID;

        if (userId == ANONYMOUS_USER_ID) {
            log.warn("[VideoSync] 인증 실패(익명) → 거부 streamId={}", streamId);
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
