package raio.payment.client.toss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment.toss")
@Slf4j
public record TossPaymentProperties(
        String secretKey,
        String baseUrl
) {
    public TossPaymentProperties {
        log.info("toss payment baseUrl: {}", baseUrl);
        
        if(secretKey == null) {
            log.warn("toss payment secretKey is null");
        }
    }
}
