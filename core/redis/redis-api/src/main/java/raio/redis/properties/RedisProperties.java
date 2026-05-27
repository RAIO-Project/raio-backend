package raio.redis.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties("app.redis")
public record RedisProperties(
        String host,
        Integer port,
        String password
) {
    public RedisProperties {
        if (host == null || host.isBlank()) {
            host = "localhost";
            log.warn("Redis host is null, using default: localhost");
        }
        if (port == null) {
            port = 6379;
            log.warn("Redis port is null, using default: 6379");
        }
    }
}