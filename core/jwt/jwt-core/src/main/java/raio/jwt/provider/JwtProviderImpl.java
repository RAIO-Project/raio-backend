package raio.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import raio.jwt.JwtProvider;
import raio.jwt.TokenType;
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

    /** JWT payload에 닉네임을 저장할 클레임 키 */
    private static final String NICKNAME_CLAIM = "nickName";
    /**
     * JWT payload에 토큰 용도를 저장할 클레임 키.
     * JOSE 헤더의 {@code typ} 와 혼동되지 않도록 다른 이름을 쓴다.
     */
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

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
    public TokenPair generate(String userId, String nickName, Set<String> roles) {
        String accessToken = buildToken(userId, nickName, roles, TokenType.ACCESS, accessTokenMaxAgeSeconds);
        String refreshToken = buildToken(userId, nickName, roles, TokenType.REFRESH, refreshTokenMaxAgeSeconds);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 토큰의 서명·만료와 용도를 검증한다.
     * 서명 불일치, 만료, 형식 오류는 모두 false 로 처리한다.
     *
     * {@code tokenType} 클레임이 없는 토큰도 false 다. 이 클레임이 도입되기 전에 발급된
     * 토큰은 모두 무효가 되므로, 배포 시 기존 세션이 끊기는 것을 감안해야 한다.
     */
    @Override
    public boolean validate(String token, TokenType expected) {
        try {
            Object type = parseToken(token).getPayload().get(TOKEN_TYPE_CLAIM);
            return expected.name().equals(type);
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
     * @param tokenType     tokenType 클레임에 담을 토큰 용도
     * @param maxAgeSeconds 토큰 만료까지의 시간(초)
     */
    private String buildToken(String userId, String nickName, Set<String> roles,
                              TokenType tokenType, long maxAgeSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId)
                .claim(NICKNAME_CLAIM, nickName)
                .claim(ROLES_CLAIM, roles)
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
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

    /**
     * 토큰의 클레임에서 nickName을 추출한다.
     */
    @Override
    public String extractNickName(String token) {
        return (String) parseToken(token).getPayload().get(NICKNAME_CLAIM);
    }
}
