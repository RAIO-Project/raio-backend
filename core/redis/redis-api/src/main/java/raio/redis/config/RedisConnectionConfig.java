package raio.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import raio.redis.properties.RedisProperties;

@Slf4j
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConnectionConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {
        var config = new RedisStandaloneConfiguration(properties.host(), properties.port());

        if (properties.password() != null && !properties.password().isBlank()) {
            config.setPassword(properties.password());
        }

        log.info("Redis 연결 설정 - host: {}, port: {}", properties.host(), properties.port());
        return new LettuceConnectionFactory(config);
    }
}