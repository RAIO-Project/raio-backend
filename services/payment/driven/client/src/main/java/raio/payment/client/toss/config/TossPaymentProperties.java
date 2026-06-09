package raio.payment.client.toss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.payment")
public record TossPaymentProperties(
        String secretKey,
        String baseUrl
) {
}
