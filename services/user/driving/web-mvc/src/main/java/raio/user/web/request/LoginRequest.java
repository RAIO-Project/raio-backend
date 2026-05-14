package raio.user.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import raio.user.application.command.LoginCommand;

public record LoginRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        String password
) {
    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
