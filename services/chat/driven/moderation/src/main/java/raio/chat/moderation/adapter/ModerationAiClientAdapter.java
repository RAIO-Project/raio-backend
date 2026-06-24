package raio.chat.moderation.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import raio.chat.application.port.AiModerationPort;
import raio.chat.application.result.ModerationResult;
import raio.chat.moderation.dto.ClassifyRequest;
import raio.chat.moderation.dto.ClassifyResponse;

import java.util.List;

/** {@link AiModerationPort} 구현 — HF 모더레이션 /classify 호출. */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ModerationAiClientAdapter implements AiModerationPort {

    private final RestClient moderationRestClient;

    @Override
    public ModerationResult classify(String chatId, String message) {
        try {
            ClassifyResponse res = moderationRestClient.post()
                    .uri("/classify")
                    .body(new ClassifyRequest(chatId, message))
                    .retrieve()
                    .body(ClassifyResponse.class);
            if (res == null) return new ModerationResult(false, List.of());
            return new ModerationResult(res.isHate(),
                    res.hateLabels() != null ? res.hateLabels() : List.of());
        } catch (RestClientResponseException e) {
            log.warn("moderation classify failed: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return new ModerationResult(false, List.of());
        } catch (Exception e) {
            log.error("moderation classify error - chatId: {}", chatId, e);
            return new ModerationResult(false, List.of());
        }
    }
}