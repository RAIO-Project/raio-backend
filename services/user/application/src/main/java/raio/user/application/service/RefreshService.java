package raio.user.application.service;

import org.springframework.stereotype.Service;
import raio.jwt.JwtProvider;
import raio.jwt.TokenPair;
import raio.user.application.usecase.RefreshUseCase;
import raio.user.domain.RefreshTokenRepository;
import raio.user.exception.UserErrorCode;

import java.time.Duration;
import java.util.Set;

@Service
public class RefreshService implements RefreshUseCase {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public RefreshService(RefreshTokenRepository refreshTokenRepository, JwtProvider jwtProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        // 토큰 서명 및 만료 검증
        if (!jwtProvider.validate(refreshToken)) {
            throw UserErrorCode.INVALID_TOKEN.exception();
        }

        Long userId = Long.parseLong(jwtProvider.extractUserId(refreshToken));
        Set<String> roles = jwtProvider.extractRoles(refreshToken);

        // Redis에 저장된 토큰과 일치하는지 확인 (탈취 여부 검증)
        String storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> UserErrorCode.INVALID_TOKEN.exception());

        if (!storedToken.equals(refreshToken)) {
            throw UserErrorCode.INVALID_TOKEN.exception();
        }

        // 슬라이딩 만료: 새 토큰 발급 + Redis TTL 14일 리셋
        TokenPair newTokenPair = jwtProvider.generate(userId.toString(), roles);
        refreshTokenRepository.save(userId, newTokenPair.refreshToken(), REFRESH_TOKEN_TTL);

        return newTokenPair;
    }
}
