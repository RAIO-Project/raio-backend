package raio.batch.scheduler.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import raio.batch.properties.BatchProperties;
import raio.batch.runner.BatchJobRunner;
import raio.batch.scheduler.BatchSchedulingConfigurer;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(BatchProperties.class)
public class BatchSchedulerConfig {
    
    @Bean
    public BatchSchedulingConfigurer batchSchedulingConfigurer(
            BatchProperties batchProperties,
            BatchJobRunner<?> batchJobRunner
    ) {
        return new BatchSchedulingConfigurer(batchProperties, batchJobRunner);
    }
}