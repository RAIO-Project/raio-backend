package raio.chat.rdb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.chat.application.port.BlacklistCommandPort;
import raio.chat.domain.Blacklist;
import raio.chat.rdb.entity.BlacklistEntity;
import raio.chat.rdb.mapper.BlacklistEntityMapper;

@Repository
@RequiredArgsConstructor
public class BlacklistCommandAdapter implements BlacklistCommandPort {

    private final BlacklistJpaRepository blacklistJpaRepository;
    private final BlacklistEntityMapper blacklistEntityMapper;


    @Override
    public void save(Blacklist blacklist) {
        var entity = blacklistEntityMapper.toEntity(blacklist);
        blacklistJpaRepository.save(entity);
    }
}
