package raio.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 게이트웨이 인증 설정.
 *
 * @param permitPaths 토큰 없이 통과시킬 경로 (Ant 패턴). 로그인·회원가입·재발급처럼
 *                    토큰을 아직 못 받은 요청과, 문서·헬스체크가 여기 들어간다.
 * @param userIdHeader     검증 결과로 다운스트림에 내려줄 사용자 ID 헤더명
 * @param nicknameHeader   닉네임 헤더명
 * @param rolesHeader      권한 헤더명 (쉼표 구분)
 */
@ConfigurationProperties("app.gateway.auth")
public record GatewayAuthProperties(
        List<String> permitPaths,
        String userIdHeader,
        String nicknameHeader,
        String rolesHeader
) {
    public GatewayAuthProperties {
        permitPaths = permitPaths != null ? permitPaths : List.of();
        userIdHeader = userIdHeader != null ? userIdHeader : "X-Auth-User-Id";
        nicknameHeader = nicknameHeader != null ? nicknameHeader : "X-Auth-Nickname";
        rolesHeader = rolesHeader != null ? rolesHeader : "X-Auth-Roles";
    }
}
