package raio.chat.application.port;

import raio.chat.application.result.ModerationResult;   // ← moderation → result

public interface AiModerationPort {
    ModerationResult classify(String chatId, String message);
}