package raio.chat.huggingface.dto;

public record BatchClassifyRequestItem(
        String chatId,
        String message
) {
}
