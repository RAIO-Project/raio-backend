package raio.main.sample.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import raio.jpa.support.SnowflakeBaseEntity;

@Entity
@Table(name = "sample")
public class Sample extends SnowflakeBaseEntity {
}
