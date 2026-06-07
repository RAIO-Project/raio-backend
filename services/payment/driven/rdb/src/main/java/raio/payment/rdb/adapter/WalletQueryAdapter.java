package raio.payment.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.payment.application.port.WalletQueryRepositoryPort;
import raio.payment.domain.Wallet;
import raio.payment.rdb.mapper.WalletEntityMapper;
import raio.payment.rdb.repository.WalletJpaRepository;

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
