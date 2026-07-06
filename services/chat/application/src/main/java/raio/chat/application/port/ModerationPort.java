package raio.chat.application.port;

import raio.chat.ChatReadModels.ModerationResult;

public interface ModerationPort {
    ModerationResult classify(String chatId, String message);
}