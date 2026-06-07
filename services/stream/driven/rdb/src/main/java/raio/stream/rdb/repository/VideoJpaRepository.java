package raio.stream.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.stream.rdb.entity.VideoEntity;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {
}
