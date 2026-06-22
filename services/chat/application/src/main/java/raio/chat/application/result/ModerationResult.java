package raio.chat.application.result;

import java.util.List;

/** AI 모더레이션 판독 결과. */
public record ModerationResult(boolean isHate, List<String> hateLabels) {
}