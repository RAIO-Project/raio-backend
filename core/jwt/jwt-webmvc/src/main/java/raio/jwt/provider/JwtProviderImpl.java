package raio.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import raio.jwt.JwtProvider;
import raio.jwt.TokenPair;
import raio.jwt.properties.JwtProperties;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JJWT 라이브러리 기반의 JwtProvider 구현체.
 * HMAC-SHA256 알고리즘으로 토큰을 서명하고 검증한다.
 */
@Component
public class JwtProviderImpl implements JwtProvider {

    /** JWT payload에 권한 목록을 저장할 클레임 키 */
    private static final String ROLES_CLAIM = "roles";

    /** HMAC-SHA256 서명에 사용할 비밀키 */
    private final SecretKey signingKey;

    /** Access Token 유효 시간 (초) */
    private final long accessTokenMaxAgeSeconds;

    /** Refresh Token 유효 시간 (초) */
    private final long refreshTokenMaxAgeSeconds;

    /**
     * JwtProperties에서 설정값을 읽어 서명키와 만료 시간을 초기화한다.
     * secretKey는 Base64 디코딩 후 HMAC 키로 변환된다.
     */
    public JwtProviderImpl(JwtProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secretKey()));
        this.accessTokenMaxAgeSeconds = properties.accessTokenMaxAgeSeconds();
        this.refreshTokenMaxAgeSeconds = properties.refreshTokenMaxAgeSeconds();
    }

    /**
     * Access Token(30분)과 Refresh Token(14일)을 생성해 TokenPair로 반환한다.
     */
    @Override
    public TokenPair generate(String userId, Set<String> roles) {
        String accessToken = buildToken(userId, roles, accessTokenMaxAgeSeconds);
        String refreshToken = buildToken(userId, roles, refreshTokenMaxAgeSeconds);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 토큰의 서명 유효성과 만료 여부를 검증한다.
     * 서명 불일치, 만료, 형식 오류 등 모든 예외를 false로 처리한다.
     */
    @Override
    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰의 subject 클레임에서 userId를 추출한다.
     */
    @Override
    public String extractUserId(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    /**
     * 토큰의 roles 클레임에서 권한 목록을 추출한다.
     * JJWT는 JSON 배열을 List로 역직렬화하므로 Set으로 변환한다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Object roles = parseToken(token).getPayload().get(ROLES_CLAIM);
        if (roles instanceof List<?> roleList) {
            return new HashSet<>(roleList.stream().map(Object::toString).toList());
        }
        return Set.of();
    }

    /**
     * 주어진 유효 시간으로 서명된 JWT 문자열을 생성한다.
     *
     * @param userId        subject에 담을 사용자 ID
     * @param roles         roles 클레임에 담을 권한 집합
     * @param maxAgeSeconds 토큰 만료까지의 시간(초)
     */
    private String buildToken(String userId, Set<String> roles, long maxAgeSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim(ROLES_CLAIM, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(maxAgeSeconds)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * JWT 문자열을 파싱해 서명을 검증하고 Claims를 반환한다.
     * 유효하지 않으면 JwtException을 던진다.
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }
}
