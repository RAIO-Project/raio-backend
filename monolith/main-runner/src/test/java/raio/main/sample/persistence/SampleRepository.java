package raio.main.sample.persistence;

import raio.main.sample.entity.Sample;
import org.springframework.data.repository.CrudRepository;

public interface SampleRepository extends CrudRepository<Sample, Long> {
}