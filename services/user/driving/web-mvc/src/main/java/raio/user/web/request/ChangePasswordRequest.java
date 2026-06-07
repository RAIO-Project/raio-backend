package raio.user.web.request;

import jakarta.validation.constraints.NotBlank;
import raio.user.application.command.ChangePasswordCommand;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
    public ChangePasswordCommand toCommand(Long userId) {
        return new ChangePasswordCommand(userId, currentPassword, newPassword);
    }
}
