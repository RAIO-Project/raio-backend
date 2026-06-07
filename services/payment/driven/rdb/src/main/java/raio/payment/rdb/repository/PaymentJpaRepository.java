package raio.payment.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raio.payment.rdb.entity.PaymentEntity;
import raio.payment.rdb.entity.type.PaymentStatusEntityType;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(String orderId);

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
