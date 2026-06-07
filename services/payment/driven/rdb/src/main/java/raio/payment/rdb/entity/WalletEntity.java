package raio.payment.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import raio.jpa.support.SnowflakeBaseTimeEntity;

@Entity
@Table(schema = "payment", name = "wallets")
public class WalletEntity extends SnowflakeBaseTimeEntity {
    
    @Column(name = "user_id", nullable = false, unique = true)
    public Long userId;

    @Column(name = "balance", nullable = false)
    public Long balance;
}
