package raio.user.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.user.application.usecase.ChangePasswordUseCase;
import raio.user.application.usecase.UpdateProfileUseCase;
import raio.user.web.request.ChangePasswordRequest;
import raio.user.web.request.UpdateProfileRequest;

@Tag(name = "User", description = "회원 정보 관련 API")
@RestController
@RequestMapping("/users")
public class UserApi {

    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    public UserApi(UpdateProfileUseCase updateProfileUseCase,
                   ChangePasswordUseCase changePasswordUseCase) {
        this.updateProfileUseCase = updateProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @Operation(summary = "회원 정보 수정", description = "닉네임, 전화번호 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "409", description = "닉네임 중복"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        updateProfileUseCase.updateProfile(request.toCommand(userId));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치")
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        changePasswordUseCase.changePassword(request.toCommand(userId));
        return ResponseEntity.ok().build();
    }
}
