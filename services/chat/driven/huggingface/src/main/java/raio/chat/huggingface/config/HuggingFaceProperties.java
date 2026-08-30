package raio.chat.huggingface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * HuggingFace 모더레이션 외부 클라이언트 설정.
 */
@ConfigurationProperties(prefix = "app.chat.moderation")
@Slf4j
public record HuggingFaceProperties(
        String baseUrl,
        String apiSecretKey,
        Duration connectTimeout,
        Duration readTimeout
) {
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);

    public HuggingFaceProperties {
        connectTimeout = connectTimeout != null ? connectTimeout : DEFAULT_CONNECT_TIMEOUT;
        readTimeout = readTimeout != null ? readTimeout : DEFAULT_READ_TIMEOUT;

        log.info("huggingface moderation baseUrl={}, connectTimeout={}, readTimeout={}",
                baseUrl, connectTimeout, readTimeout);
        if (apiSecretKey == null) log.warn("huggingface apiSecretKey is null");
    }
}
