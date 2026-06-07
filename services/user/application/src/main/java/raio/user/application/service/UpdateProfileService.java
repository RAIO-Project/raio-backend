package raio.user.application.service;

import org.springframework.stereotype.Service;
import raio.user.application.command.UpdateProfileCommand;
import raio.user.application.port.UserRepository;
import raio.user.application.usecase.UpdateProfileUseCase;
import raio.user.domain.Users;
import raio.user.exception.UserErrorCode;

@Service
public class UpdateProfileService implements UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateProfile(UpdateProfileCommand command) {
        Users user = userRepository.findById(command.userId())
                .orElseThrow(UserErrorCode.USER_NOT_FOUND::exception);

        if (command.nickname() != null && !command.nickname().equals(user.getNickname())
                && userRepository.existsByNickname(command.nickname())) {
            throw UserErrorCode.NICKNAME_ALREADY_EXISTS.exception();
        }

        userRepository.update(user.withProfile(command.nickname(), command.phoneNumber()));
    }
}
