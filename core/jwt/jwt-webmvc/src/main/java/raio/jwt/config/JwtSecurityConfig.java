package raio.jwt.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import raio.jwt.filter.JwtAuthenticationFilter;
import raio.jwt.filter.JwtFilterProperties;
import raio.jwt.properties.JwtProperties;

/**
 * Spring Security 기본 설정 및 JWT 필터를 등록하는 설정 클래스.
 * jwt-webmvc 모듈을 의존성에 추가하면 이 설정이 자동으로 적용된다.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({JwtProperties.class, JwtFilterProperties.class})
public class JwtSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public JwtSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * JWT 기반 REST API에 적합한 SecurityFilterChain을 구성한다.
     *
     * <ul>
     *   <li>STATELESS: 서버가 세션을 생성하지 않음 (JWT가 상태를 대체)</li>
     *   <li>CSRF 비활성화: 세션 쿠키를 사용하지 않으므로 CSRF 위협 없음</li>
     *   <li>HTTP Basic / Form Login 비활성화: REST API에서 불필요</li>
     *   <li>JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 삽입하여
     *       요청마다 SecurityContext를 먼저 세팅</li>
     * </ul>
     *
     * @Order(10): 서비스별 SecurityFilterChain보다 낮은 우선순위(숫자가 클수록 낮음)로
     * 동작해 각 서비스가 자신의 인가 규칙을 덮어쓸 수 있게 한다.
     */
    @Bean
    @Order(10)
    public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
