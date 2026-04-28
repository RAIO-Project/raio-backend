package raio.batch.config;

import raio.batch.core.runner.BatchJobRunner;
import raio.batch.properties.BatchProperties;
import raio.batch.properties.job.JobProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class BatchSchedulingConfig implements SchedulingConfigurer {
    
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    
    private final BatchProperties batchProperties;
    private final BatchJobRunner batchJobRunner;
    
    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        
        if (!batchProperties.enabled()) {
            log.info("[배치 스케줄] 비활성화 상태 → 스케줄 등록 안함 (app.batch.enabled=false)");
            return;
        }
        
        if (batchProperties.jobs().isEmpty()) {
            log.warn("[배치 스케줄] 등록된 잡 설정이 없습니다. app.batch.jobs 확인 필요");
            return;
        }
        
        log.info("[배치 스케줄] 총 {}개 잡 등록 시작", batchProperties.jobs().size());
        
        for (Map.Entry<String, JobProperties> e : batchProperties.jobs().entrySet()) {
            String jobName = e.getKey();
            JobProperties jp = e.getValue();
            
            if (!jp.enabled()) {
                log.info("[배치 스케줄] 비활성 잡 → 건너뜀 jobName={}", jobName);
                continue;
            }
            
            Runnable task = () -> {
                try {
                    log.info("[배치 실행] 시작 → jobName={}, params={}", jobName, jp.parameters());
                    batchJobRunner.run(jobName, jp.parameters());
                    log.info("[배치 실행] 완료 → jobName={}", jobName);
                } catch (Exception ex) {
                    log.error("[배치 실행] 실패 ❌ jobName={}", jobName, ex);
                }
            };
            
            if (jp.hasCron()) {
                CronTrigger trigger = new CronTrigger(jp.cron(), KST);
                taskRegistrar.addTriggerTask(task, trigger);
                log.info("[배치 스케줄] CRON 등록 완료 jobName={}, cron={}", jobName, jp.cron());
                continue;
            }
            
            if (jp.hasFixedDelay()) {
                taskRegistrar.addTriggerTask(task, new FixedDelayTrigger(jp.fixedDelayMs()));
                log.info("[배치 스케줄] fixedDelay 등록 완료 jobName={}, delay={}ms",
                        jobName, jp.fixedDelayMs());
                continue;
            }
            
            log.warn("[배치 스케줄] 스케줄 정보 없음 → 등록 안함 jobName={}", jobName);
        }
    }
    
    // Trigger
    static final class FixedDelayTrigger implements Trigger {
        
        private final long delayMs;
        
        FixedDelayTrigger(long delayMs) {
            this.delayMs = delayMs;
        }
        
        @Override
        public Instant nextExecution(TriggerContext triggerContext) {
            
            Instant lastCompletion = triggerContext.lastCompletion();
            Instant base = (lastCompletion != null)
                    ? lastCompletion
                    : Instant.now();
            
            return base.plusMillis(delayMs);
        }
    }
}
