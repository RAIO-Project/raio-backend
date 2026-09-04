package raio.jwt;

/**
 * 토큰의 용도. {@code tokenType} 클레임으로 서명 대상에 포함된다.
 *
 * <p>이 구분이 없으면 두 토큰이 클레임상 동일해져(만료 시간만 다름) RefreshToken 을
 * {@code Authorization: Bearer} 로 그대로 쓸 수 있게 된다. 그러면 AccessToken 을 30분으로
 * 짧게 잡은 의미가 사라지고 실질 유효 기간이 RefreshToken 의 14일이 된다.
 */
public enum TokenType {

    /** API 인증용. 짧은 만료(기본 30분). */
    ACCESS,

    /** AccessToken 재발급 전용. 긴 만료(기본 14일). 인증 헤더로는 쓸 수 없다. */
    REFRESH
}
