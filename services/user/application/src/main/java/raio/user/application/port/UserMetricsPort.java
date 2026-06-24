package raio.user.application.port;

public interface UserMetricsPort {
    void incrementRegisteredUser();
    void incrementTokenRefresh();
}
