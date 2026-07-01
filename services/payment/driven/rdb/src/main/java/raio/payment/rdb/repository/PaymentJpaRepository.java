package raio.payment.rdb.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raio.payment.rdb.entity.PaymentEntity;
import raio.payment.rdb.entity.type.PaymentStatusEntityType;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(String orderId);

    @Query("SELECT p FROM PaymentEntity p WHERE p.status = :status AND p.externalTid IS NOT NULL")
    List<PaymentEntity> findApprovingWithPaymentKey(@Param("status") PaymentStatusEntityType status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentEntity p WHERE p.id = :id")
    Optional<PaymentEntity> findByIdForUpdate(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE PaymentEntity p
               SET p.status      = :status,
                   p.externalTid = :externalTid,
                   p.failReason  = :failReason
             WHERE p.id = :id
            """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") PaymentStatusEntityType status,
            @Param("externalTid") String externalTid,
            @Param("failReason") String failReason
    );
}
