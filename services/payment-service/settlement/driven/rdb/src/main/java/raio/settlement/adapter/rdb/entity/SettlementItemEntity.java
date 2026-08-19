package raio.settlement.adapter.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(schema = "payment", name = "settlement_items")
public class SettlementItemEntity {

    @Id
    @Column(name = "id", length = 36)
    public String id;

    @Column(name = "settlement_id", nullable = false, length = 36)
    public String settlementId;

    @Column(name = "donation_id", nullable = false)
    public Long donationId;

    @Column(name = "gross_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal grossAmount;

    @Column(name = "applied_fee_rate", nullable = false, precision = 5, scale = 4)
    public BigDecimal appliedFeeRate;

    @Column(name = "fee_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal feeAmount;

    @Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal netAmount;

    @Column(name = "revenue_occurred_at", nullable = false)
    public Instant revenueOccurredAt;
}
