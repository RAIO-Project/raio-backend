package raio.batch.properties.job;

import java.util.Map;

public record JobProperties(
        boolean enabled,
        // 크론(선택): 예) "0 0/5 * * * */
        String cron,
        // fixedDelayMs(선택): 크론 대신 고정 딜레이 트리거를 쓰고 싶을 때
        Long fixedDelayMs,
        // 잡 공통 파라미터(선택): 실행 시 JobParameters로 변환됨
        Map<String, String> parameters
) {
    public JobProperties {
        if (parameters == null) {
            parameters = Map.of();
        }
        if (cron != null && !cron.isBlank()) {
            cron = cron.strip();
        } else {
            cron = null;
        }
    }
    
    public boolean hasCron() {
        return cron != null && !cron.isBlank();
    }
    
    public boolean hasFixedDelay() {
        return fixedDelayMs != null && fixedDelayMs > 0;
    }
}