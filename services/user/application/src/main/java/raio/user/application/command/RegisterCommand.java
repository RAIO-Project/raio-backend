package raio.user.application.command;

/** 회원가입 유스케이스 입력 데이터 */
public record RegisterCommand(
        String email,
        String password,
        String nickname,
        String phoneNumber
) {}
