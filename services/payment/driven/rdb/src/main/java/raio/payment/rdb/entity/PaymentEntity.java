package raio.payment.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import raio.jpa.support.SnowflakeBaseTimeEntity;
import raio.payment.rdb.entity.type.PaymentMethodEntityType;
import raio.payment.rdb.entity.type.PaymentMethodEntityTypeConverter;
import raio.payment.rdb.entity.type.PaymentStatusEntityType;
import raio.payment.rdb.entity.type.PaymentStatusEntityTypeConverter;
import raio.payment.rdb.entity.type.PgProviderEntityType;
import raio.payment.rdb.entity.type.PgProviderEntityTypeConverter;

@Entity
@Table(schema = "payment", name = "payments")
public class PaymentEntity extends SnowflakeBaseTimeEntity {

    @Column(name = "order_id", unique = true)
    public String orderId;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "amount", nullable = false)
    public Long amount;

    @Convert(converter = PaymentStatusEntityTypeConverter.class)
    @Column(name = "status", nullable = false)
    public PaymentStatusEntityType status;

    @Convert(converter = PaymentMethodEntityTypeConverter.class)
    @Column(name = "method")
    public PaymentMethodEntityType method;

    @Convert(converter = PgProviderEntityTypeConverter.class)
    @Column(name = "pg_provider")
    public PgProviderEntityType pgProvider;

    @Column(name = "external_tid")
    public String externalTid;

    @Column(name = "fail_reason")
    public String failReason;
}
