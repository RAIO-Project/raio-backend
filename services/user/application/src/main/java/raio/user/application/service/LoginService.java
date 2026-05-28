package raio.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raio.jwt.JwtProvider;
import raio.jwt.TokenPair;
import raio.user.application.command.LoginCommand;
import raio.user.application.port.RefreshTokenRepository;
import raio.user.application.port.UserRepository;
import raio.user.application.usecase.LoginUseCase;
import raio.user.domain.Users;
import raio.user.domain.type.UserStatus;
import raio.user.exception.UserErrorCode;

import java.util.Set;

@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        JwtProvider jwtProvider,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenPair login(LoginCommand command) {
        Users user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> UserErrorCode.INVALID_EMAIL_OR_PASSWORD.exception());

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw UserErrorCode.INVALID_EMAIL_OR_PASSWORD.exception();
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw UserErrorCode.USER_SUSPENDED.exception();
        }
        if (user.getStatus() == UserStatus.REMOVED) {
            throw UserErrorCode.USER_REMOVED.exception();
        }

        Set<String> roles = Set.of(user.getRole().name());
        TokenPair tokenPair = jwtProvider.generate(user.getId().toString(), roles);
        refreshTokenRepository.save(user.getId(), tokenPair.refreshToken());

        return tokenPair;
    }
}
