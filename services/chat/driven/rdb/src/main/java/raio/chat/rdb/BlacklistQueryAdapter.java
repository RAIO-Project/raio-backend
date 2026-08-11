package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.BlacklistQueryPort;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class BlacklistQueryAdapter implements BlacklistQueryPort {

    private final BlacklistJpaRepository blacklistJpaRepository;

    @Override
    public boolean existsActiveByUserId(String userId) {
        return blacklistJpaRepository.existsByUserIdAndUnblockAtAfter(Long.parseLong(userId), Instant.now());
    }
}
