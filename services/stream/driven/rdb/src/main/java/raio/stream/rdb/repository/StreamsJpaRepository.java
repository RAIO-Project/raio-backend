package raio.stream.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.stream.rdb.entity.StreamsEntity;

public interface StreamsJpaRepository extends JpaRepository<StreamsEntity, Long> {
}
