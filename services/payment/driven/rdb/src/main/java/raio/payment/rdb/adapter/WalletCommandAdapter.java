package raio.payment.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.payment.application.port.WalletCommandRepositoryPort;
import raio.payment.domain.Wallet;
import raio.payment.rdb.entity.WalletEntity;
import raio.payment.rdb.mapper.WalletEntityMapper;
import raio.payment.rdb.repository.WalletJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalletCommandAdapter implements WalletCommandRepositoryPort {
    
    private final WalletJpaRepository walletJpaRepository;
    private final WalletEntityMapper walletEntityMapper;
    
    @Override
    public boolean existsById(String id) {
        return walletJpaRepository.existsById(Long.parseLong(id));
    }
    
    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity saved = walletJpaRepository.saveAndFlush(walletEntityMapper.toEntity(wallet));
        return walletEntityMapper.toDomain(saved);
    }
    
    @Override
    public Optional<Wallet> increaseBalance(String walletId, Long amount) {
        Long id = Long.parseLong(walletId);
        
        int updated = walletJpaRepository.increaseBalance(id, amount);
        if (updated == 0) {
            return Optional.empty();
        }
        
        return walletJpaRepository.findById(id)
                .map(walletEntityMapper::toDomain);
    }
    
    @Override
    public Optional<Wallet> decreaseBalance(String walletId, Long amount) {
        Long id = Long.parseLong(walletId);
        
        int updated = walletJpaRepository.decreaseBalance(id, amount);
        if (updated == 0) {
            return Optional.empty();
        }
        
        return walletJpaRepository.findById(id)
                .map(walletEntityMapper::toDomain);
    }
}