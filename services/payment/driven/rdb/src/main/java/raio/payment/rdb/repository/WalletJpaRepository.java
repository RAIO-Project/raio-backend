package raio.payment.rdb.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import raio.payment.rdb.entity.WalletEntity;

import java.util.Optional;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
    
    Optional<WalletEntity> findByUserId(Long userId);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WalletEntity w
               SET w.balance = w.balance + :amount
             WHERE w.id = :walletId
            """)
    int increaseBalance(
            @Param("walletId") Long walletId,
            @Param("amount") Long amount
    );
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE WalletEntity w
               SET w.balance = w.balance - :amount
             WHERE w.id = :walletId
               AND w.balance >= :amount
            """)
    int decreaseBalance(
            @Param("walletId") Long walletId,
            @Param("amount") Long amount
    );
    
    /**
     * 정말 비관적 락 필요할 때만 사용
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT w
              FROM WalletEntity w
             WHERE w.id = :walletId
            """)
    Optional<WalletEntity> findByIdForUpdate(@Param("walletId") Long walletId);
}