package raio.chat.application.port;

import raio.chat.ChatReadModels.ModerationResult;

import java.util.List;
import java.util.Map;

public interface ModerationPort {
    ModerationResult classify(String chatId, String message);

    Map<String, ModerationResult> classifyBatch(List<ModerationRequestItem> items);

    record ModerationRequestItem(String chatId, String message){}
}