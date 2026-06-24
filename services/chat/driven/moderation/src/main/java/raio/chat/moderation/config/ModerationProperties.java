package raio.chat.moderation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** AI 모더레이션(HF) 외부 클라이언트 설정. (app.payment.toss 와 동일 패턴) */
@ConfigurationProperties(prefix = "app.chat.moderation")
@Slf4j
public record ModerationProperties(
        String baseUrl,
        String apiSecretKey,
        String consumerName
) {
    public ModerationProperties {
        log.info("moderation baseUrl: {}", baseUrl);
        if (apiSecretKey == null) log.warn("moderation apiSecretKey is null");
        if (consumerName == null || consumerName.isBlank()) consumerName = "worker-1";
    }
}