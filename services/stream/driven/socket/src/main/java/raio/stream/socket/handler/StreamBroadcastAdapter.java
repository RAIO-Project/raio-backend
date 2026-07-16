package raio.stream.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.socket.relay.RelayMessage;
import raio.socket.relay.StreamRelayChannel;
import raio.stream.application.port.StreamBroadcastPort;
import raio.stream.socket.relay.ViewerCountMessage;

import java.time.Instant;

/**
 * {@link StreamBroadcastPort} 의 Redis publish 구현. 공용 채널(stream:events:{id})로 발행하고
 * core RelaySubscriber 가 /topic/streams/{id} 로 전달한다. (chat/donation 과 같은 채널을 공유)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamBroadcastAdapter implements StreamBroadcastPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastViewerCount(String streamId, long viewerCount) {
        publish(streamId, ViewerCountMessage.of(streamId, viewerCount, Instant.now()));
    }

    private void publish(String streamId, RelayMessage payload) {
        try {
            stringRedisTemplate.convertAndSend(
                    StreamRelayChannel.redisChannel(streamId), objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            log.error("publish 실패 - type: {}, streamId: {}", payload.type(), streamId, e);
        }
    }
}
