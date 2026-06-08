package raio.redis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis pub/sub 메시지 리스너 컨테이너 (범용).
 * 리스너/구독 채널은 각 서비스가 자신의 MessageListener 를
 * 이 컨테이너에 등록해서 사용한다. (chat 의 ChatRelaySubscriber 가 @PostConstruct 로 등록)
 */
@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}