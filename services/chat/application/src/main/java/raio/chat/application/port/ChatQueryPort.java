package raio.chat.application.port;

public interface ChatQueryPort {
    String findUserIdById(String id);
    long countBlockedByUserId(String id);
}
