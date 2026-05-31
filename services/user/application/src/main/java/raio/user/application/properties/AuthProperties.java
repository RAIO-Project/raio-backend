package raio.user.application.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("user.auth")
public record AuthProperties(Duration refreshTokenTtl) {
    public AuthProperties {
        if (refreshTokenTtl == null) refreshTokenTtl = Duration.ofDays(14);
    }
}
