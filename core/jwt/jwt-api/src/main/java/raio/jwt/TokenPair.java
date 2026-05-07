package raio.jwt;

/**
 * 로그인 성공 시 발급되는 Access Token / Refresh Token 쌍.
 *
 * @param accessToken  짧은 유효기간(30분)의 API 인증용 토큰
 * @param refreshToken 긴 유효기간(14일)의 Access Token 재발급용 토큰
 */
public record TokenPair(
        String accessToken,
        String refreshToken
) {}
