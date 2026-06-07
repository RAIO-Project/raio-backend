package raio.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raio.user.application.command.ChangePasswordCommand;
import raio.user.application.port.UserRepository;
import raio.user.application.usecase.ChangePasswordUseCase;
import raio.user.domain.Users;
import raio.user.exception.UserErrorCode;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        Users user = userRepository.findById(command.userId())
                .orElseThrow(UserErrorCode.USER_NOT_FOUND::exception);

        if (!passwordEncoder.matches(command.currentPassword(), user.getPassword())) {
            throw UserErrorCode.INCORRECT_PASSWORD.exception();
        }

        userRepository.update(user.withPassword(passwordEncoder.encode(command.newPassword())));
    }
}
