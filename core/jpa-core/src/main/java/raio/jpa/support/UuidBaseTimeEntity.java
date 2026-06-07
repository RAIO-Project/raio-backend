package raio.jpa.support;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@SuperBuilder
@NoArgsConstructor
@Getter
@MappedSuperclass
public abstract class UuidBaseTimeEntity extends UuidBaseCreatedEntity {
    
    @LastModifiedDate
    private Instant updatedAt;
}