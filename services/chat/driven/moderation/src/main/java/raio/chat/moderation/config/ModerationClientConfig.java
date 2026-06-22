package raio.chat.moderation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ModerationProperties.class)
public class ModerationClientConfig {

    @Bean
    public RestClient moderationRestClient(ModerationProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("X-API-Key", properties.apiSecretKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}