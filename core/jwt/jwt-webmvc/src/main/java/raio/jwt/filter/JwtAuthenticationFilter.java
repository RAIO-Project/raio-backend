package raio.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import raio.jwt.JwtProvider;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 모든 HTTP 요청에 대해 JWT Access Token을 검증하는 보안 필터.
 *
 * 요청당 정확히 한 번만 실행되도록 OncePerRequestFilter를 상속한다.
 * 토큰이 유효하면 SecurityContext에 인증 정보를 주입하고,
 * 없거나 유효하지 않으면 예외 없이 다음 필터로 넘긴다
 * (이후 Spring Security가 인증 부재를 처리해 401을 반환).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Authorization 헤더에서 토큰을 식별하는 Bearer 접두사 */
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final JwtFilterProperties filterProperties;

    /** Ant 패턴(예: /auth/**)으로 URL을 매칭하는 유틸리티 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtProvider jwtProvider, JwtFilterProperties filterProperties) {
        this.jwtProvider = jwtProvider;
        this.filterProperties = filterProperties;
    }

    /**
     * 요청 경로가 yml에 설정된 제외 목록과 일치하면 이 필터를 건너뛴다.
     * 경로(Ant 패턴)와 HTTP 메서드를 모두 비교하며, methods가 "*"이면 전체 허용.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return filterProperties.excludePaths().stream()
                .anyMatch(excluded -> {
                    boolean pathMatches = pathMatcher.match(excluded.path(), path);
                    boolean methodMatches = "*".equals(excluded.methods())
                            || excluded.methods().toLowerCase().contains(method.toLowerCase());
                    return pathMatches && methodMatches;
                });
    }

    /**
     * Authorization 헤더에서 Bearer 토큰을 추출하고 유효성을 검사한다.
     * 유효한 토큰이면 userId와 권한을 SecurityContext에 등록한다.
     * 토큰이 없거나 유효하지 않아도 예외를 던지지 않고 필터 체인을 계속 진행한다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        if (token != null && jwtProvider.validate(token)) {
            String userId = jwtProvider.extractUserId(token);
            Set<String> roles = jwtProvider.extractRoles(token);

            // 권한 이름 앞에 "ROLE_" 접두사를 붙여 Spring Security 규약에 맞춤
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            // principal에 userId를 담아 컨트롤러에서 authentication.getPrincipal()로 꺼낼 수 있게 한다
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거한 순수 토큰 문자열을 반환한다.
     * 헤더가 없거나 Bearer 형식이 아니면 null을 반환한다.
     */
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
