package raio.batch.core.runner;

import raio.batch.core.exception.BatchRunException;
import raio.batch.core.factory.JobParametersFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SpringBatchJobRunner implements BatchJobRunner {
    
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;
    private final JobParametersFactory jobParametersFactory;
    
    @Override
    public JobExecution run(String jobName, Map<String, String> parameters) {
        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("jobName is null/blank");
        }
        
        try {
            Job job = jobRegistry.getJob(jobName.strip());
            var jobParameters = jobParametersFactory.build(parameters);
            
            log.info("[BATCH] run jobName={}, params={}", jobName, parameters);
            return jobLauncher.run(job, jobParameters);
            
        } catch (JobExecutionAlreadyRunningException e) {
            // 운영에서 자주 필요한 케이스: 중복 트리거 방지
            log.warn("[BATCH] already running: jobName={}", jobName, e);
            throw new BatchRunException("Job already running: " + jobName, e);
            
        } catch (JobInstanceAlreadyCompleteException e) {
            // run.id를 넣었으면 보통 안 걸리지만, 정책에 따라 발생 가능
            log.warn("[BATCH] already complete: jobName={}", jobName, e);
            throw new BatchRunException("Job instance already complete: " + jobName, e);
            
        } catch (JobRestartException e) {
            log.error("[BATCH] restart error: jobName={}", jobName, e);
            throw new BatchRunException("Job restart error: " + jobName, e);
            
        } catch (Exception e) {
            log.error("[BATCH] run error: jobName={}", jobName, e);
            throw new BatchRunException("Job run error: " + jobName, e);
        }
    }
}
