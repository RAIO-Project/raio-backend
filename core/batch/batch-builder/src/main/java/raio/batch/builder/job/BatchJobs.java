package raio.batch.builder.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import raio.batch.builder.step.FunctionalStepBuilder;

@RequiredArgsConstructor
public class BatchJobs {
    
    private final JobRepository jobRepository;
    private final FunctionalStepBuilder stepBuilder;
    
    public FunctionalJobBuilder job(String jobName) {
        return new FunctionalJobBuilder(jobRepository, stepBuilder, jobName);
    }
}