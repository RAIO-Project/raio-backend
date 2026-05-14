package raio.user.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.user.application.usecase.LoginUseCase;
import raio.user.application.usecase.LogoutUseCase;
import raio.user.application.usecase.RefreshUseCase;
import raio.user.application.usecase.RegisterUseCase;
import raio.user.web.request.LoginRequest;
import raio.user.web.request.RefreshRequest;
import raio.user.web.request.RegisterRequest;
import raio.user.web.response.TokenPairResponse;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
public class AuthApi {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshUseCase refreshUseCase;

    public AuthApi(RegisterUseCase registerUseCase,
                   LoginUseCase loginUseCase,
                   LogoutUseCase logoutUseCase,
                   RefreshUseCase refreshUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.refreshUseCase = refreshUseCase;
    }

    @Operation(summary = "회원가입", description = "이메일/비밀번호로 회원가입. 생성된 userId 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복")
    })
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody @Valid RegisterRequest request) {
        Long userId = registerUseCase.register(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호 검증 후 AccessToken + RefreshToken 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치"),
            @ApiResponse(responseCode = "403", description = "정지 또는 탈퇴한 계정")
    })
    @PostMapping("/login")
    public TokenPairResponse login(@RequestBody @Valid LoginRequest request) {
        return TokenPairResponse.from(loginUseCase.login(request.toCommand()));
    }

    @Operation(summary = "로그아웃", description = "Redis에서 RefreshToken 삭제. Authorization 헤더에 AccessToken 필요")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        Long userId = Long.parseLong((String) authentication.getPrincipal());
        logoutUseCase.logout(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 재발급", description = "RefreshToken으로 새 AccessToken + RefreshToken 발급 (슬라이딩 만료)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 RefreshToken")
    })
    @PostMapping("/refresh")
    public TokenPairResponse refresh(@RequestBody @Valid RefreshRequest request) {
        return TokenPairResponse.from(refreshUseCase.refresh(request.refreshToken()));
    }
}
