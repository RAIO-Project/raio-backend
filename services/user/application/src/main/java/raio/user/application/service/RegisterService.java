package raio.user.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raio.user.application.command.RegisterCommand;
import raio.user.application.port.PaymentCommandPort;
import raio.user.application.port.UserRepository;
import raio.user.application.usecase.RegisterUseCase;
import raio.user.domain.Users;
import raio.user.domain.type.UserRole;
import raio.user.domain.type.UserStatus;
import raio.user.exception.UserErrorCode;

@Service
@Slf4j
public class RegisterService implements RegisterUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentCommandPort paymentCommandPort;

    public RegisterService(UserRepository userRepository, PasswordEncoder passwordEncoder, PaymentCommandPort paymentCommandPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentCommandPort = paymentCommandPort;
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
        
        Users savedUser = userRepository.save(user);
        
        try {
            paymentCommandPort.createWallet(String.valueOf(savedUser.getId()));
        } catch (Exception e) {
            log.error("지갑 생성 요청 실패. userId={}", savedUser.getId(), e);
        }
        
        return savedUser.getId();
    }
}
