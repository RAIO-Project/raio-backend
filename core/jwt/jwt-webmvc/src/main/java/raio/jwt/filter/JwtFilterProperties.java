package raio.jwt.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * club.jwt.filter.yml의 app.auth.filter.* 설정을 바인딩하는 Properties.
 * JWT 검사를 건너뛸 경로 목록을 관리한다.
 *
 * @param excludePaths JWT 인증 필터를 적용하지 않을 경로 목록
 */
@ConfigurationProperties("app.auth.filter")
public record JwtFilterProperties(
        List<ExcludePath> excludePaths
) {
    /**
     * 필터에서 제외할 단일 경로 설정.
     *
     * @param path        제외할 URL 패턴 (Ant 패턴 지원, 예: "/auth/**")
     * @param methods     제외할 HTTP 메서드 (쉼표 구분 또는 "*"로 전체 허용)
     * @param description 경로 용도 설명 (설정 파일 가독성용, 로직에는 미사용)
     */
    public record ExcludePath(
            String path,
            String methods,
            String description
    ) {}
}
