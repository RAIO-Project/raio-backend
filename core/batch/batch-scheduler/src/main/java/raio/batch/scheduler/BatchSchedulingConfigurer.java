package raio.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import raio.batch.properties.BatchProperties;
import raio.batch.runner.BatchJobRunner;

@Slf4j
@RequiredArgsConstructor
public class BatchSchedulingConfigurer implements SchedulingConfigurer {
    
    private final BatchProperties batchProperties;
    private final BatchJobRunner<?> batchJobRunner;
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (!batchProperties.enabled()) {
            log.info("[BATCH-SCHEDULER] disabled");
        }
        
        batchProperties.jobs().forEach((jobName, job) -> {
            if (!job.hasCron()) {
                log.info("[BATCH-SCHEDULER] skip job. jobName={}, reason=no cron", jobName);
                return;
            }
            
            taskRegistrar.addCronTask(
                    () -> run(jobName),
                    job.cron()
            );
            
            log.info(
                    "[BATCH-SCHEDULER] registered job. jobName={}, cron={}",
                    jobName,
                    job.cron()
            );
        });
    }
    
    private void run(String jobName) {
        try {
            batchJobRunner.run(jobName);
        } catch (Exception e) {
            log.error("[BATCH-SCHEDULER] scheduled job failed. jobName={}", jobName, e);
        }
    }
}
