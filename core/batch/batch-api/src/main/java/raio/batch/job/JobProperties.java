package raio.batch.job;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record JobProperties(
        Boolean enabled,
        // On-demand 실행 방식 가능 여부
        Boolean onDemandEnabled,
        // 크론(선택): 예) "0 0/5 * * * */
        String cron
) {
    public JobProperties {
        enabled = enabled == null || enabled;
        onDemandEnabled = onDemandEnabled != null && onDemandEnabled;
        
        if (cron != null && !cron.isBlank()) {
            cron = cron.strip();
        } else {
            log.warn("job's cron is null or empty");
            
            cron = null;
        }
        
        if (!onDemandEnabled && cron == null) {
            log.warn(
                    "Enabled batch job must have either a cron expression or on-demand execution enabled."
            );
        }
    }
    
    public boolean hasCron() {
        return cron != null && !cron.isBlank();
    }
}