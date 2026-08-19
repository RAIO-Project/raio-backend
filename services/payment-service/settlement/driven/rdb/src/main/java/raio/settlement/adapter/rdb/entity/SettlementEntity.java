package raio.settlement.adapter.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityType;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityTypeConverter;
import raio.settlement.adapter.rdb.entity.type.SettlementStatusEntityType;
import raio.settlement.adapter.rdb.entity.type.SettlementStatusEntityTypeConverter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(schema = "payment", name = "settlements")
public class SettlementEntity {

    @Id
    @Column(name = "id", length = 36)
    public String id;

    @Column(name = "streamer_id", nullable = false)
    public Long streamerId;

    @Convert(converter = SettlementCycleEntityTypeConverter.class)
    @Column(name = "cycle", nullable = false)
    public SettlementCycleEntityType cycle;

    @Column(name = "period_start_at", nullable = false)
    public Instant periodStartAt;

    @Column(name = "period_end_at", nullable = false)
    public Instant periodEndAt;

    @Column(name = "gross_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal grossAmount;

    @Column(name = "applied_fee_rate", nullable = false, precision = 5, scale = 4)
    public BigDecimal appliedFeeRate;

    @Column(name = "fee_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal feeAmount;

    @Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal netAmount;

    @Convert(converter = SettlementStatusEntityTypeConverter.class)
    @Column(name = "status", nullable = false)
    public SettlementStatusEntityType status;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;
}
