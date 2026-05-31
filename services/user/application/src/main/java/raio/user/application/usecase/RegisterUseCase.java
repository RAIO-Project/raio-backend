package raio.user.application.usecase;

import raio.user.application.command.RegisterCommand;

public interface RegisterUseCase {
    /** 회원가입 후 생성된 userId 반환 */
    Long register(RegisterCommand command);
}
