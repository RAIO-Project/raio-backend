package raio.batch.core.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import raio.batch.core.exception.BatchRunException;
import raio.batch.core.factory.JobParametersFactory;
import raio.batch.runner.BatchJobRunner;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SpringBatchJobRunner implements BatchJobRunner<JobExecution> {
    
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;
    private final JobParametersFactory jobParametersFactory;
    
    @Override
    public JobExecution run(String jobName) {
        return run(jobName,  null);
    }
    
    @Override
    public JobExecution run(String jobName, Map<String, String> parameters) {
        var normalizedJobName = normalizeJobName(jobName);
        var safeParameters = safeParameters(parameters);
        
        try {
            Job job = jobRegistry.getJob(normalizedJobName);
            var jobParameters = jobParametersFactory.build(safeParameters);
            
            log.info("[BATCH] job start. jobName={}, parameters={}", normalizedJobName, safeParameters);
            
            JobExecution execution = jobLauncher.run(job, jobParameters);
            
            log.info(
                    "[BATCH] job launched. jobName={}, executionId={}, status={}, exitStatus={}",
                    normalizedJobName,
                    execution.getId(),
                    execution.getStatus(),
                    execution.getExitStatus().getExitCode()
            );
            
            return execution;
            
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("[BATCH] job already running. jobName={}", normalizedJobName, e);
            throw new BatchRunException("Job already running: " + normalizedJobName, e);
            
        } catch (JobInstanceAlreadyCompleteException e) {
            log.warn("[BATCH] job instance already complete. jobName={}", normalizedJobName, e);
            throw new BatchRunException("Job instance already complete: " + normalizedJobName, e);
            
        } catch (JobRestartException e) {
            log.error("[BATCH] job restart failed. jobName={}", normalizedJobName, e);
            throw new BatchRunException("Job restart failed: " + normalizedJobName, e);
            
        } catch (Exception e) {
            log.error("[BATCH] job run failed. jobName={}", normalizedJobName, e);
            throw new BatchRunException("Job run failed: " + normalizedJobName, e);
        }
    }
    
    private String normalizeJobName(String jobName) {
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName must not be null or blank");
        }
        
        return jobName.strip();
    }
    
    private Map<String, String> safeParameters(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return Map.of();
        }
        
        return Map.copyOf(parameters);
    }
}