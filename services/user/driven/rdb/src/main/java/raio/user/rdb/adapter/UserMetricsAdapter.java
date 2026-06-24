package raio.user.rdb.adapter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raio.monitoring.recorder.MetricsRecorder;
import raio.user.application.port.RefreshTokenRepository;
import raio.user.application.port.UserMetricsPort;

@Component
@RequiredArgsConstructor
public class UserMetricsAdapter implements UserMetricsPort {

    private final MetricsRecorder metricsRecorder;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void init() {
        metricsRecorder.recordGauge("raio.user.active.session",
                () -> refreshTokenRepository.countAll());
    }

    @Override
    public void incrementRegisteredUser() {
        metricsRecorder.incrementCounter("raio.user.registered");
    }

    @Override
    public void incrementTokenRefresh() {
        metricsRecorder.incrementCounter("raio.user.token.refresh");
    }
}
