package raio.chat.huggingface.dto;

import java.util.List;

public record BatchClassifyResultDto(
        String chatId,
        String message,
        boolean isHate,
        List<String> hateLabels
) {
}
