package raio.chat.socket.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class ChatWebSocketDto {
    private ChatWebSocketDto() {}

    // 클라 → 서버 (채팅 발송)
    public record ChatSendCommand(
            @NotBlank @Size(max = 500) String message
    ) {}

    // 서버 → 클라 (채팅 브로드캐스트)
    public record ChatBroadcastEvent(
            String type,           // "CHAT"
            String streamId,
            String userId,
            String senderNickname,
            String message,
            Boolean isBlocked,
            String blockReason,
            Instant createdAt
    ) {}

    // 서버 → 클라 (입장/퇴장)
    public record StreamPresenceEvent(
            String type,           // "JOIN" | "LEAVE"
            String streamId,
            String userId,
            String nickname,
            Instant occurredAt
    ) {}
}