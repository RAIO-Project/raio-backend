package raio.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * main.jwt.yml의 jwt.* 설정을 바인딩하는 Properties.
 *
 * @param secretKey                Base64로 인코딩된 HMAC-SHA256 서명 키 (환경변수 JWT_SECRET_KEY)
 * @param accessTokenMaxAgeSeconds Access Token 유효 시간 (초 단위, 기본 1800 = 30분)
 * @param refreshTokenMaxAgeSeconds Refresh Token 유효 시간 (초 단위, 기본 1209600 = 14일)
 */
@ConfigurationProperties("jwt")
public record JwtProperties(
        String secretKey,
        long accessTokenMaxAgeSeconds,
        long refreshTokenMaxAgeSeconds
) {}
