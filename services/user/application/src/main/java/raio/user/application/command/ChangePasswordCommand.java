package raio.user.application.command;

public record ChangePasswordCommand(
        Long userId,
        String currentPassword,
        String newPassword
) {}
