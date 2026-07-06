package raio.batch.runner;

import java.util.Map;

/**
 * Spring Batch Job 실행 인터페이스.
 *
 * @param <R> 배치 실행 결과 타입
 */
public interface BatchJobRunner<R> {
    
    /**
     * 지정한 이름의 Job을 기본 JobParameters로 실행한다.
     *
     * @param jobName 등록된 Job 이름
     * @return 실행 결과
     */
    R run(String jobName);
    
    /**
     * 지정한 이름의 Job을 추가 JobParameters와 함께 실행한다.
     * 전달된 parameters는 구현체에서 Spring Batch JobParameters로 변환된다.
     *
     * @param jobName 등록된 Job 이름
     * @param parameters 추가 Job 파라미터
     * @return 실행 결과
     */
    R run(String jobName, Map<String, String> parameters);
}