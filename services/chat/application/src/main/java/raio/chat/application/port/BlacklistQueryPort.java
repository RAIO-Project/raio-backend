package raio.chat.application.port;

public interface BlacklistQueryPort {
    boolean existsActiveByUserId(String userId);
}
