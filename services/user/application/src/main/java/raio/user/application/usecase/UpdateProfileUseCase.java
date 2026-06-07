package raio.user.application.usecase;

import raio.user.application.command.UpdateProfileCommand;

public interface UpdateProfileUseCase {
    void updateProfile(UpdateProfileCommand command);
}
