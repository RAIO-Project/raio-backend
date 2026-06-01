package raio.user.application.usecase;

import raio.jwt.TokenPair;
import raio.user.application.command.LoginCommand;

public interface LoginUseCase {
    /** 로그인 후 AccessToken + RefreshToken 반환 */
    TokenPair login(LoginCommand command);
}
