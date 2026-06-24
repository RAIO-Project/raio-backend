package raio.user.rdb.adapter;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import raio.monitoring.recorder.MetricsRecorder;
import raio.user.application.port.RefreshTokenRepository;
import raio.user.application.port.UserMetricsPort;

@Component
public class UserMetricsAdapter implements UserMetricsPort {

    private final MetricsRecorder metricsRecorder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserMetricsAdapter(MetricsRecorder metricsRecorder, RefreshTokenRepository refreshTokenRepository) {
        this.metricsRecorder = metricsRecorder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

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
