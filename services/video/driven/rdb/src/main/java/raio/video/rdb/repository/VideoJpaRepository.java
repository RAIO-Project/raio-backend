package raio.video.rdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raio.video.rdb.entity.VideoEntity;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {
}
