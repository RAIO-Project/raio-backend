package raio.chat.huggingface.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(HuggingFaceProperties.class)
public class HuggingFaceClientConfig {

    @Bean
    public RestClient huggingFaceRestClient(HuggingFaceProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("X-API-Key", properties.apiSecretKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
