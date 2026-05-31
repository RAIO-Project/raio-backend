package raio.user.application.usecase;

public interface LogoutUseCase {
    /** Redis에서 RefreshToken 삭제 */
    void logout(Long userId);
}
