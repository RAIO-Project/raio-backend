package raio.monitoring.recorder;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class MetricsRecorder {

    private final MeterRegistry meterRegistry;

    // 누적 카운트. 증가만 가능. ex) 회원가입 수, 로그인 횟수, 결제 횟수
    public void incrementCounter(String name, String... tags) {
        Counter.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    // 현재 값. 증가/감소 가능. ex) 현재 접속자 수, 대기열 크기
    public void recordGauge(String name, Supplier<Number> valueSupplier, String... tags) {
        Gauge.builder(name, valueSupplier)
                .tags(tags)
                .register(meterRegistry);
    }

    // 완료된 작업의 실행 시간 측정. ex) API 응답 시간, 외부 서비스 호출 시간
    public <T> T recordTimer(String name, Supplier<T> action, String... tags) {
        return Timer.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(action);
    }

    // 값의 분포 측정. ex) 결제 금액 분포, 요청 payload 크기
    public void recordSummary(String name, double value, String... tags) {
        DistributionSummary.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(value);
    }

    // 현재 진행 중인 작업의 경과 시간 측정. ex) 배치 작업 실행 시간
    public <T> T recordLongTask(String name, Supplier<T> action, String... tags) {
        LongTaskTimer timer = LongTaskTimer.builder(name)
                .tags(tags)
                .register(meterRegistry);
        LongTaskTimer.Sample sample = timer.start();
        try {
            return action.get();
        } finally {
            sample.stop();
        }
    }
}
