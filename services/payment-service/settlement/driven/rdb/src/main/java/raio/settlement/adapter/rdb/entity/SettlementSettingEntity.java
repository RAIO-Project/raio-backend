package raio.settlement.adapter.rdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityType;
import raio.settlement.adapter.rdb.entity.type.SettlementCycleEntityTypeConverter;

import java.time.Instant;

@Entity
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

    @Column(name = "active", nullable = false)
    public boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}
