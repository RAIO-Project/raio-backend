package raio.redis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import java.time.Duration;

/**
 * Redis Streams 메시지 리스너 컨테이너 (범용).
 * 소비 대상 스트림/그룹/리스너는 각 서비스가 자신의 StreamListener 를
 * 이 컨테이너에 등록해서 사용한다. (RedisListenerConfig 의 pub/sub 컨테이너와 동일한 패턴)
 */
@Configuration
@RequiredArgsConstructor
public class RedisStreamListenerConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer() {
        var options = StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();
        var container = StreamMessageListenerContainer.create(connectionFactory, options);
        container.start();
        return container;
    }
}