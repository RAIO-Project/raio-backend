package raio.user.application.command;

public record UpdateProfileCommand(
        Long userId,
        String nickname,
        String phoneNumber
) {}
