package raio.donation.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.donation.application.port.DonationBroadcastPort;
import raio.donation.domain.Donations;
import raio.donation.socket.relay.DonationMessage;
import raio.socket.relay.StreamRelayChannel;

import java.time.Instant;

/**
 * {@link DonationBroadcastPort} 의 Redis publish 구현.
 *
 * <p>후원 메시지를 공용 채널(stream:events:{id})로 발행. 수신/전달은 core 의 공용 RelaySubscriber 가
 * 처리한다(인스턴스 간 fan-out). 기존 SimpMessagingTemplate 직접 전송은 단일 인스턴스 한정이라 교체함.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DonationBroadcastAdapter implements DonationBroadcastPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastDonation(Long streamId, Donations donations, String senderNickname) {
        if (streamId == null) {
            log.warn("streamId 가 null 이라 publish 생략");
            return;
        }
        var payload = new DonationMessage(
                String.valueOf(donations.getStreamId()),
                String.valueOf(donations.getSenderId()),
                senderNickname,
                donations.getAmount(),
                donations.getMessage(),
                donations.isBlocked(),
                Instant.now()
        );
        try {
            String json = objectMapper.writeValueAsString(payload);
            stringRedisTemplate.convertAndSend(StreamRelayChannel.redisChannel(streamId), json);
        } catch (Exception e) {
            log.error("후원 메시지 publish 실패 - streamId: {}", streamId, e);
        }
    }
}