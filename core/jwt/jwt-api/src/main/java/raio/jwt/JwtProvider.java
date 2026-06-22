package raio.jwt;

import java.util.Set;

/**
 * JWT 토큰 생성 및 검증을 담당하는 인터페이스.
 * 실제 구현체(JwtProviderImpl)는 jwt-webmvc 모듈에 위치하며,
 * 이 인터페이스만 의존하면 구현 라이브러리(JJWT)에 직접 의존하지 않아도 된다.
 */
public interface JwtProvider {

    /**
     * userId, nickName, roles를 담은 Access Token / Refresh Token 쌍을 생성한다.
     *
     * @param userId   토큰 subject에 저장할 사용자 ID
     * @param nickName 토큰 claim에 저장할 사용자 닉네임
     * @param roles    토큰 claim에 저장할 권한 목록 (예: {"USER", "ADMIN"})
     * @return accessToken + refreshToken 쌍
     */
    TokenPair generate(String userId, String nickName, Set<String> roles);

    /**
     * 토큰의 서명과 만료 시간을 검증한다.
     *
     * @param token 검증할 JWT 문자열
     * @return 유효하면 true, 서명 불일치·만료·형식 오류이면 false
     */
    boolean validate(String token);

    /**
     * 토큰의 subject 클레임에서 userId를 추출한다.
     *
     * @param token 유효한 JWT 문자열
     * @return 토큰에 담긴 사용자 ID
     */
    String extractUserId(String token);

    /**
     * 토큰의 roles 클레임에서 권한 목록을 추출한다.
     *
     * @param token 유효한 JWT 문자열
     * @return 권한 이름의 집합 (예: {"USER"})
     */
    Set<String> extractRoles(String token);

    /**
     * 토큰의 클레임에서 nickName를 추출한다.
     *
     * @param token 유효한 JWT 문자열
     * @return 토큰에 담긴 사용자 nickName
     */
    String extractNickName(String token);
}
