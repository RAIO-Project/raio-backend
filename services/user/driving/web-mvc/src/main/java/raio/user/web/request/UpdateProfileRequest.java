package raio.user.web.request;

import raio.user.application.command.UpdateProfileCommand;

public record UpdateProfileRequest(
        String nickname,
        String phoneNumber
) {
    public UpdateProfileCommand toCommand(Long userId) {
        return new UpdateProfileCommand(userId, nickname, phoneNumber);
    }
}
