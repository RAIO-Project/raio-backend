package raio.chat.huggingface.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import raio.chat.ChatReadModels.ModerationResult;
import raio.chat.application.port.ModerationPort;
import raio.chat.huggingface.dto.ClassifyRequest;
import raio.chat.huggingface.dto.ClassifyResponse;

import java.util.List;

/** {@link ModerationPort} 구현 — HuggingFace 모더레이션 /classify 호출. (PaymentTossClientAdapter 패턴) */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HuggingFaceClientAdapter implements ModerationPort {

    private final RestClient huggingFaceRestClient;

    @Override
    public ModerationResult classify(String chatId, String message) {
        try {
            ClassifyResponse res = huggingFaceRestClient.post()
                    .uri("/classify")
                    .body(new ClassifyRequest(chatId, message))
                    .retrieve()
                    .body(ClassifyResponse.class);
            if (res == null) return new ModerationResult(false, List.of());
            return new ModerationResult(res.isHate(),
                    res.hateLabels() != null ? res.hateLabels() : List.of());
        } catch (RestClientResponseException e) {
            log.warn("huggingface classify failed: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return new ModerationResult(false, List.of());
        } catch (Exception e) {
            log.error("huggingface classify error - chatId: {}", chatId, e);
            return new ModerationResult(false, List.of());
        }
    }
}
