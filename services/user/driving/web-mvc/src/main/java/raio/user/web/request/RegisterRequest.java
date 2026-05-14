package raio.user.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import raio.user.application.command.RegisterCommand;

public record RegisterRequest(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, max = 30)
        String password,

        @Size(max = 50)
        String nickname,

        @Size(max = 20)
        String phoneNumber
) {
    public RegisterCommand toCommand() {
        return new RegisterCommand(email, password, nickname, phoneNumber);
    }
}
