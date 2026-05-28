package raio.user.application.service;

import org.springframework.stereotype.Service;
import raio.user.application.usecase.LogoutUseCase;
import raio.user.application.port.RefreshTokenRepository;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void logout(Long userId) {
        // Redis에서 RefreshToken 삭제 → 이후 재발급 요청 불가
        refreshTokenRepository.delete(userId);
    }
}
