package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import raio.chat.application.port.BlacklistCommandPort;
import raio.chat.domain.Blacklist;
import raio.chat.rdb.mapper.BlacklistEntityMapper;

import java.time.Duration;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class BlacklistCommandAdapter implements BlacklistCommandPort {

    private final BlacklistJpaRepository blacklistJpaRepository;
    private final BlacklistEntityMapper blacklistEntityMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Blacklist blacklist) {
        var entity = blacklistEntityMapper.toEntity(blacklist);
        blacklistJpaRepository.save(entity);

        // 캐시 반영은 커밋 이후에. 롤백되면 DB엔 없는데 캐시에만 차단이 남는 상황을 막는다.
        afterCommit(() -> cacheBlocked(blacklist));
    }

    /** unblockAt까지 남은 시간을 TTL로 걸어 두면 해제 시각에 키가 저절로 사라진다. */
    private void cacheBlocked(Blacklist blacklist) {
        String key = BlacklistCache.key(blacklist.getUserId());
        Duration ttl = Duration.between(Instant.now(), blacklist.getUnblockAt());

        if (ttl.isPositive()) {
            redisTemplate.opsForValue().set(key, BlacklistCache.BLOCKED, ttl);
        } else {
            // 이미 해제 시각이 지난 차단이면 캐싱할 게 없다. 남아 있을 수 있는 이전 키만 정리.
            redisTemplate.delete(key);
        }
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
