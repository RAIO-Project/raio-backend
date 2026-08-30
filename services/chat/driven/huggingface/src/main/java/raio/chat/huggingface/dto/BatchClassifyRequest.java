package raio.chat.huggingface.dto;

import java.util.List;

public record BatchClassifyRequest(
        List<BatchClassifyRequestItem> items
) {
}
