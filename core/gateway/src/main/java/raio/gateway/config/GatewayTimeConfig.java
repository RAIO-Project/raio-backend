package raio.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.time.MillisecondsSupplier;
import raio.time.SystemMilliseconds;

/**
 * {@link MillisecondsSupplier} 빈 등록.
 *
 * <p>time-util 은 Spring 에 의존하지 않는 순수 모듈이라 스스로 빈을 노출하지 않는다
 * (Snowflake 도 {@code new SystemMilliseconds()} 로 직접 만들어 쓴다).
 * RequestLoggingFilter 가 생성자 주입으로 요구하므로 여기서 제공한다.
 * 이게 없으면 게이트웨이가 기동하지 못한다.
 */
@Configuration
public class GatewayTimeConfig {

    @Bean
    public MillisecondsSupplier millisecondsSupplier() {
        return new SystemMilliseconds();
    }
}
