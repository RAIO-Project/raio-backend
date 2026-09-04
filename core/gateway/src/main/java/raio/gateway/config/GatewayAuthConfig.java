package raio.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import raio.gateway.properties.GatewayAuthProperties;
import raio.jwt.properties.JwtProperties;

/**
 * 게이트웨이 인증에 필요한 Properties 등록.
 *
 * <p>{@link JwtProperties} 는 서블릿 쪽에서 JwtSecurityConfig 가 등록해주지만, 게이트웨이는
 * jwt-webmvc 에 의존하지 않으므로(서블릿 스택 유입 방지) 여기서 직접 등록해야 한다.
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, GatewayAuthProperties.class})
public class GatewayAuthConfig {
}
