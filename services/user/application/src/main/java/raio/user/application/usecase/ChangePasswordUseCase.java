package raio.user.application.usecase;

import raio.user.application.command.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void changePassword(ChangePasswordCommand command);
}
