package raio.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import raio.gateway.properties.GatewayAuthProperties;
import raio.jwt.JwtProvider;
import raio.jwt.TokenType;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * 게이트웨이 JWT 인증.
 *
 * 검증에 성공하면 사용자 정보를 헤더로 실어 다운스트림에 넘긴다. 각 서비스는 토큰을 다시
 * 파싱하지 않고 이 헤더만 읽으면 된다.
 *
 * 클라이언트가 보낸 인증 헤더는 먼저 지운다
 * 다운스트림이 {@code X-Auth-*} 를 신뢰하므로, 클라이언트가 직접 이 헤더를 붙여 보내면
 * 아무나 임의의 사용자로 행세할 수 있다. 검증 전에 무조건 제거하는 것이 이 필터에서
 * 가장 중요한 부분이다. 통과 경로(permitPaths)에서도 예외 없이 지운다.
 *
 * WebSocket 은 여기서 인증되지 않는다
 * STOMP 는 HTTP 업그레이드가 끝난 뒤 CONNECT 프레임에 토큰을 실어 보내므로 게이트웨이가
 * 볼 수 없다. {@code /ws/**} 는 통과시키고, 인증은 앱의 StompAuthChannelInterceptor 가 맡는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final GatewayAuthProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 위조 방지 — 검증 전에 클라이언트가 보낸 인증 헤더를 지운다.
        ServerHttpRequest sanitized = request.mutate()
                .headers(h -> {
                    h.remove(properties.userIdHeader());
                    h.remove(properties.nicknameHeader());
                    h.remove(properties.rolesHeader());
                })
                .build();

        if (isPermitted(request)) {
            return chain.filter(exchange.mutate().request(sanitized).build());
        }

        String token = extractBearerToken(request);
        if (token == null || !jwtProvider.validate(token, TokenType.ACCESS)) {
            log.debug("[GW-AUTH] 인증 실패 - {} {}", request.getMethod(), request.getPath());
            return unauthorized(exchange);
        }

        String userId = jwtProvider.extractUserId(token);
        String nickname = jwtProvider.extractNickName(token);
        Set<String> roles = jwtProvider.extractRoles(token);

        ServerHttpRequest authenticated = sanitized.mutate()
                .header(properties.userIdHeader(), userId)
                .header(properties.nicknameHeader(), nickname == null ? "" : nickname)
                .header(properties.rolesHeader(), String.join(",", roles))
                .build();

        return chain.filter(exchange.mutate().request(authenticated).build());
    }

    /** CORS 프리플라이트는 Authorization 헤더를 싣지 않으므로 항상 통과시킨다. */
    private boolean isPermitted(ServerHttpRequest request) {
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return true;
        }
        String path = request.getPath().value();
        return properties.permitPaths().stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private String extractBearerToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * 라우팅 필터보다 앞서야 한다. NettyRoutingFilter 가 Ordered.LOWEST_PRECEDENCE 이므로
     * 기본값보다 충분히 앞선 값을 준다.
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
