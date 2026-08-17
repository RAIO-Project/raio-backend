package raio.wallet.adapter.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import raio.jpa.support.LongBaseCreatedEntity;
import raio.wallet.adapter.rdb.entity.type.PointHistoryEntityType;
import raio.wallet.adapter.rdb.entity.type.PointHistoryEntityTypeConverter;

@Entity
@Table(schema = "payment", name = "point_histories")
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

    @Column(name = "source_id")
    public Long sourceId;
}
