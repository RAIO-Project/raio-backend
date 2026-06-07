package raio.stream.application.port;

import raio.stream.domain.Videos;

import java.util.Optional;

public interface VideoRepositoryPort {
    Videos save(Videos video);
    Optional<Videos> findById(Long id);
}
