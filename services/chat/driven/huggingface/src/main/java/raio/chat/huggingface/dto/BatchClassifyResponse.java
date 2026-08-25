package raio.chat.huggingface.dto;

import java.util.List;

public record BatchClassifyResponse(
        List<BatchClassifyResultDto> results
) {
}
