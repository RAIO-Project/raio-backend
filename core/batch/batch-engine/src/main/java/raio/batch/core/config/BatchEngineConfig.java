package raio.batch.core.config;

import org.springframework.batch.core.JobExecution;
import raio.batch.core.factory.JobParametersFactory;
import raio.batch.core.runner.SpringBatchJobRunner;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.batch.runner.BatchJobRunner;

@Configuration
public class BatchEngineConfig {
    
    @Bean
    public JobParametersFactory jobParametersFactory() {
        return new JobParametersFactory();
    }
    
    @Bean
    public BatchJobRunner<JobExecution> batchJobRunner(
            JobRegistry jobRegistry,
            JobLauncher jobLauncher,
            JobParametersFactory jobParametersFactory
    ) {
        return new SpringBatchJobRunner(jobRegistry, jobLauncher, jobParametersFactory);
    }
}
