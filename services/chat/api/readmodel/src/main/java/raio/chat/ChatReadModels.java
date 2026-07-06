package raio.chat;

import java.util.List;

public final class ChatReadModels {

    private ChatReadModels() {
    }

    /**
     * AI 모더레이션 판정 결과.
     * isHate=true 이면 혐오로 판정되어 블라인드 대상이 된다.
     */
    public record ModerationResult(
            boolean isHate,
            List<String> hateLabels
    ) {
    }
}