package raio.video.application.port;

import raio.video.domain.Videos;

import java.util.Optional;

public interface VideoRepositoryPort {
    Videos save(Videos video);
    Optional<Videos> findById(Long id);
}
