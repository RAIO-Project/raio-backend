package raio.batch.ondemand.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import raio.batch.ondemand.BatchOndemandExecutor;
import raio.batch.properties.BatchProperties;
import raio.batch.runner.BatchJobRunner;

@AutoConfiguration
public class BatchOndemandAutoConfiguration {
    
    @Bean
    public BatchOndemandExecutor batchOndemandExecutor(
            BatchProperties batchProperties,
            BatchJobRunner<JobExecution> batchJobRunner
    ) {
        return new BatchOndemandExecutor(batchProperties, batchJobRunner);
    }
}