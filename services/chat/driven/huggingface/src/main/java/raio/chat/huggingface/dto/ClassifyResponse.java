package raio.chat.huggingface.dto;

import java.util.List;

public record ClassifyResponse(String chatId, String message, boolean isHate, List<String> hateLabels) {
}
