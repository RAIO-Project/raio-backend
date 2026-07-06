package raio.batch.ondemand;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import raio.batch.properties.BatchProperties;
import raio.batch.runner.BatchJobRunner;

@RequiredArgsConstructor
public class BatchOndemandExecutor {
    
    private final BatchProperties batchProperties;
    private final BatchJobRunner<JobExecution> batchJobRunner;
    
    public JobExecution run(String jobName) {
        var job = batchProperties.jobs().get(jobName);
        
        if (job == null || !job.enabled()) {
            throw new IllegalArgumentException("Unknown or disabled batch job: " + jobName);
        }
        
        if (!job.onDemandEnabled()) {
            throw new IllegalStateException("Manual execution is disabled. jobName=" + jobName);
        }
        
        return batchJobRunner.run(jobName);
    }
}
