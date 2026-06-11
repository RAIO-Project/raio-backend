package raio.stream.rdb;

import org.springframework.stereotype.Repository;
import raio.stream.application.port.VideoRepositoryPort;
import raio.stream.domain.Videos;
import raio.stream.rdb.entity.VideoEntity;
import raio.stream.rdb.repository.VideoJpaRepository;

import java.util.Optional;

@Repository
public class VideoRepositoryAdapter implements VideoRepositoryPort {

    private final VideoJpaRepository videoJpaRepository;

    public VideoRepositoryAdapter(VideoJpaRepository videoJpaRepository) {
        this.videoJpaRepository = videoJpaRepository;
    }

    @Override
    public Videos save(Videos video) {
        return videoJpaRepository.save(VideoEntity.from(video)).toDomain();
    }

    @Override
    public Optional<Videos> findById(Long id) {
        return videoJpaRepository.findById(id).map(VideoEntity::toDomain);
    }
}
