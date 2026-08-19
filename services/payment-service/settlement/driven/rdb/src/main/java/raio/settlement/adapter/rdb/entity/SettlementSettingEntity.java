package raio.settlement.adapter.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityType;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityTypeConverter;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(schema = "payment", name = "settlement_settings")
public class SettlementSettingEntity {

    @Id
    @Column(name = "streamer_id")
    public Long streamerId;

    @Convert(converter = SettlementCycleEntityTypeConverter.class)
    @Column(name = "current_cycle", nullable = false)
    public SettlementCycleEntityType currentCycle;

    @Convert(converter = SettlementCycleEntityTypeConverter.class)
    @Column(name = "pending_cycle")
    public SettlementCycleEntityType pendingCycle;

    @Column(name = "pending_cycle_effective_at")
    public Instant pendingCycleEffectiveAt;

    @Column(name = "next_settlement_at", nullable = false)
    public Instant nextSettlementAt;

    @Column(name = "last_settled_at")
    public Instant lastSettledAt;

    @Column(name = "active", nullable = false)
    public boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}
