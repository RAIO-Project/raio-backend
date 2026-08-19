package raio.chat.huggingface.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(HuggingFaceProperties.class)
public class HuggingFaceClientConfig {

    /**
     * 모더레이션 전용 RestClient.
     *
     * <p>기본 RestClient 는 타임아웃이 걸려 있지 않아, HF Space 가 응답하지 않으면 호출 스레드가
     * 무기한 대기한다. 모더레이션 워커는 큐를 순차 소비하는 단일 스레드이므로 그 대기가 곧
     * 큐 정체로 이어진다.
     */
    @Bean
    public RestClient huggingFaceRestClient(HuggingFaceProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("X-API-Key", properties.apiSecretKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
