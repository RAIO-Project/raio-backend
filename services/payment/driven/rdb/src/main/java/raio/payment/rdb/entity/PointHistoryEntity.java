package raio.payment.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import raio.jpa.support.LongBaseCreatedEntity;
import raio.payment.rdb.entity.type.PointHistoryEntityType;
import raio.payment.rdb.entity.type.PointHistoryEntityTypeConverter;

@Entity
@Table(schema = "payment", name = "point_histories")
@EntityListeners(AuditingEntityListener.class)
public class PointHistoryEntity extends LongBaseCreatedEntity {
    
    @Column(name = "wallet_id", nullable = false)
    public Long walletId;
    
    @Column(name = "user_id", nullable = false)
    public Long userId;
    
    @Convert(converter = PointHistoryEntityTypeConverter.class)
    @Column(name = "type", nullable = false, length = 20)
    public PointHistoryEntityType type;
    
    @Column(name = "amount", nullable = false)
    public Long amount;
    
    @Column(name = "balance_snapshot", nullable = false)
    public Long balanceSnapshot;
}
