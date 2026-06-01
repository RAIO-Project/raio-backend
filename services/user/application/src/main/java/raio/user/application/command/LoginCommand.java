package raio.user.application.command;

/** 로그인 유스케이스 입력 데이터 */
public record LoginCommand(
        String email,
        String password
) {}
