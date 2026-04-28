package raio.batch.properties;

import raio.batch.properties.job.JobProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Slf4j
@ConfigurationProperties("app.batch")
public record BatchProperties(
        boolean enabled,
        Map<String, JobProperties> jobs
) {
    public BatchProperties {
        if (jobs == null) {
            jobs = Map.of();
        }
        if (!enabled) {
            log.info("배치 미사용 상태 (app.batch.enabled=false)");
        } else {
            log.info("배치 사용 상태. 설정된 jobs: {}", jobs.keySet());
        }
    }
}
