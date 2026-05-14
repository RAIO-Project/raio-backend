package raio.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raio.jwt.JwtProvider;
import raio.jwt.TokenPair;
import raio.user.application.command.LoginCommand;
import raio.user.application.usecase.LoginUseCase;
import raio.user.domain.RefreshTokenRepository;
import raio.user.domain.UserRepository;
import raio.user.domain.Users;
import raio.user.domain.type.UserStatus;
import raio.user.exception.UserErrorCode;

import java.time.Duration;
import java.util.Set;

@Service
public class LoginService implements LoginUseCase {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

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
        // 이메일로 사용자 조회 (없으면 보안상 동일한 메시지 반환)
        Users user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> UserErrorCode.INVALID_EMAIL_OR_PASSWORD.exception());

        // 비밀번호 검증
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw UserErrorCode.INVALID_EMAIL_OR_PASSWORD.exception();
        }

        // 계정 상태 확인
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw UserErrorCode.USER_SUSPENDED.exception();
        }
        if (user.getStatus() == UserStatus.REMOVED) {
            throw UserErrorCode.USER_REMOVED.exception();
        }

        // 토큰 생성 및 RefreshToken Redis 저장
        Set<String> roles = Set.of(user.getRole().name());
        TokenPair tokenPair = jwtProvider.generate(user.getId().toString(), roles);
        refreshTokenRepository.save(user.getId(), tokenPair.refreshToken(), REFRESH_TOKEN_TTL);

        return tokenPair;
    }
}
