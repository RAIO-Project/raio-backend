package raio.user.application.usecase;

import raio.jwt.TokenPair;

public interface RefreshUseCase {
    /** RefreshToken 검증 후 새 TokenPair 발급 (슬라이딩 만료 적용) */
    TokenPair refresh(String refreshToken);
}
