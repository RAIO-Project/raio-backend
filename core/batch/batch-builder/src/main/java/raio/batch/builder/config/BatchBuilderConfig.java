package raio.batch.builder.config;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import raio.batch.builder.job.BatchJobs;
import raio.batch.builder.step.FunctionalStepBuilder;

@AutoConfiguration
public class BatchBuilderConfig {
    
    @Bean
    public FunctionalStepBuilder functionalStepBuilder(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new FunctionalStepBuilder(jobRepository, transactionManager);
    }
    
    @Bean
    public BatchJobs batchJobs(
            JobRepository jobRepository,
            FunctionalStepBuilder stepBuilder
    ) {
        return new BatchJobs(jobRepository, stepBuilder);
    }
}