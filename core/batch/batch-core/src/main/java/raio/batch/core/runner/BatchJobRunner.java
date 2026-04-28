package raio.batch.core.runner;

import org.springframework.batch.core.JobExecution;

import java.util.Map;

/**
 * 이미 동일한 jobName + parameters(멱등 키)가 최근 실행된 적이 있으면 실행을 스킵(또는 예외)할 수 있는 훅을 두고 싶다면
 * 이후 확장 포인트로 추가 가능.
 */
public interface BatchJobRunner {
    
    /**
     * jobName으로 잡을 찾아 실행한다.
     * - parameters는 JobParameters로 변환된다.
     */
    JobExecution run(String jobName, Map<String, String> parameters);
}
