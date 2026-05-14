package raio.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raio.user.application.command.RegisterCommand;
import raio.user.application.usecase.RegisterUseCase;
import raio.user.domain.UserRepository;
import raio.user.domain.Users;
import raio.user.domain.type.UserRole;
import raio.user.domain.type.UserStatus;
import raio.user.exception.UserErrorCode;

@Service
public class RegisterService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Long register(RegisterCommand command) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(command.email())) {
            throw UserErrorCode.EMAIL_ALREADY_EXISTS.exception();
        }

        // 닉네임 중복 확인 (닉네임이 입력된 경우에만)
        if (command.nickname() != null && userRepository.existsByNickname(command.nickname())) {
            throw UserErrorCode.NICKNAME_ALREADY_EXISTS.exception();
        }

        Users user = Users.builder()
                .email(command.email())
                .password(passwordEncoder.encode(command.password()))
                .nickname(command.nickname())
                .phoneNumber(command.phoneNumber())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user).getId();
    }
}
