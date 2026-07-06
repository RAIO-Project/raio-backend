package raio.chat.huggingface.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** HuggingFace 모더레이션 외부 클라이언트 설정. (app.payment.toss 와 동일 패턴) */
@ConfigurationProperties(prefix = "app.chat.moderation")
@Slf4j
public record HuggingFaceProperties(
        String baseUrl,
        String apiSecretKey
) {
    public HuggingFaceProperties {
        log.info("huggingface moderation baseUrl: {}", baseUrl);
        if (apiSecretKey == null) log.warn("huggingface apiSecretKey is null");
    }
}
