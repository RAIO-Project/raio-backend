package raio.batch.job;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record JobProperties(
        // 크론(선택): 예) "0 0/5 * * * */
        String cron
) {
    public JobProperties {
        if (cron != null && !cron.isBlank()) {
            cron = cron.strip();
        } else {
            log.warn("job's cron is null or empty");
            
            cron = null;
        }
    }
    
    public boolean hasCron() {
        return cron != null && !cron.isBlank();
    }
}