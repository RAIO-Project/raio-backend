package raio.wallet.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.wallet.application.port.WalletQueryRepositoryPort;
import raio.wallet.domain.Wallet;
import raio.wallet.adapter.rdb.mapper.WalletEntityMapper;
import raio.wallet.adapter.rdb.repository.WalletJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalletQueryAdapter implements WalletQueryRepositoryPort {

    private final WalletJpaRepository walletJpaRepository;
    private final WalletEntityMapper walletEntityMapper;

    @Override
    public Optional<Wallet> findById(String id) {
        return walletJpaRepository.findById(Long.parseLong(id))
                .map(walletEntityMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserId(String userId) {
        return walletJpaRepository.findByUserId(Long.parseLong(userId))
                .map(walletEntityMapper::toDomain);
    }
}
