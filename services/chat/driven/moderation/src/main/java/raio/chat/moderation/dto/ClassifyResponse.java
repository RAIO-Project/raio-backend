package raio.chat.moderation.dto;

import java.util.List;

public record ClassifyResponse(String chatId, String message, boolean isHate, List<String> hateLabels) {
}