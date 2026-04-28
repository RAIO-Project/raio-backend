package raio.batch.core.factory;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.Instant;
import java.util.Map;

public class JobParametersFactory {
    
    /**
     * 공통 규칙:
     * - 전달된 map의 모든 값을 string 파라미터로 주입
     * - 재실행/실행 구분을 위해 run.id(현재 epoch millis) 기본 주입
     * 주의:
     * - Spring Batch는 같은 식별 파라미터면 "이미 완료된 인스턴스"로 실행이 막힐 수 있다.
     * - 매 실행마다 새 인스턴스로 돌리려면 run.id 같은 파라미터를 포함시키는 게 안전하다.
     */
    public JobParameters build(Map<String, String> parameters) {
        JobParametersBuilder b = new JobParametersBuilder();
        
        if (parameters != null) {
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                String key = e.getKey();
                String val = e.getValue();
                
                if (key == null || key.isBlank()) continue;
                key = key.strip();
                if (val == null) continue;
                
                b.addString(key, val);
            }
        }
        
        b.addLong("run.id", Instant.now().toEpochMilli());
        return b.toJobParameters();
    }
}
