package raio.chat.huggingface.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import raio.chat.ChatReadModels.ModerationResult;
import raio.chat.application.port.ModerationPort;
import raio.chat.huggingface.dto.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static raio.chat.exception.ChatErrorCode.MODERATION_FAILED;

/**
 * {@link ModerationPort} 구현 — HuggingFace 모더레이션 /classify 호출.
 *
 * <p>실패 시 예외를 던진다. 이전에는 "혐오 아님"을 돌려주며 실패를 삼켰는데, 그러면 호출부가
 * <b>판정 결과가 없다</b>는 사실을 알 수 없어 재시도할 수도, 집계할 수도 없다. 타임아웃이
 * 잦아지면 모더레이션이 조용히 꺼진 것과 같은 상태가 되고 지표에도 잡히지 않는다.
 *
 * <p>채팅 노출은 이 호출과 무관하다. 메시지는 이미 브로드캐스트된 뒤이고 모더레이션은
 * 사후 블라인드이므로, 여기서 예외를 던져도 사용자에게 보이는 채팅은 그대로 유지된다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HuggingFaceClientAdapter implements ModerationPort {

    private final RestClient huggingFaceRestClient;

    @Override
    public ModerationResult classify(String chatId, String message) {
        ClassifyResponse res = requestClassify(chatId, message);

        if (res == null) {
            log.warn("huggingface classify 응답 본문 없음 - chatId: {}", chatId);
            throw MODERATION_FAILED.exception();
        }

        return new ModerationResult(
                res.isHate(),
                res.hateLabels() != null ? res.hateLabels() : List.of()
        );
    }

    private ClassifyResponse requestClassify(String chatId, String message) {
        try {
            return huggingFaceRestClient.post()
                    .uri("/classify")
                    .body(new ClassifyRequest(chatId, message))
                    .retrieve()
                    .body(ClassifyResponse.class);

        } catch (RestClientResponseException e) {
            log.warn("huggingface classify 실패 - status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw MODERATION_FAILED.exception(e);

        } catch (Exception e) {
            log.warn("huggingface classify 오류 - chatId: {}, cause: {}", chatId, e.toString());
            throw MODERATION_FAILED.exception(e);
        }
    }

    @Override
    public Map<String, ModerationResult> classifyBatch(List<ModerationRequestItem> items) {
        if(items.isEmpty()) {
            return Map.of();
        }

        BatchClassifyResponse response = requestClassifyBatch(items);

        if(response == null || response.results() == null) {
            log.warn("huggingface classify-batch 응답 본문 없음 - size: {}", items.size());
            throw MODERATION_FAILED.exception();
        }

        return response.results().stream()
                .collect(Collectors.toMap(
                        BatchClassifyResultDto::chatId,
                        toModerationResult(),
                        (first, second) -> second
                ));
    }

    private static Function<BatchClassifyResultDto, ModerationResult> toModerationResult() {
        return r -> new ModerationResult(r.isHate(), r.hateLabels() != null ? r.hateLabels() : List.of());
    }

    private BatchClassifyResponse requestClassifyBatch(List<ModerationRequestItem> items) {
        try{
            var body = new BatchClassifyRequest(
                    items.stream()
                            .map(i -> new BatchClassifyRequestItem(i.chatId(), i.message()))
                            .toList()
            );

            return huggingFaceRestClient.post()
                    .uri("/classify-batch")
                    .body(body)
                    .retrieve()
                    .body(BatchClassifyResponse.class);
        }catch (RestClientResponseException e) {
            log.warn("huggingface classify-batch 실패 - status={}, body={}, size={}", e.getStatusCode(), e.getResponseBodyAsString(), items.size());
            throw MODERATION_FAILED.exception(e);
        }catch (Exception e) {
            log.warn("huggingface classify-batch 오류 - size: {}, cause: {}", items.size(), e.toString());
            throw MODERATION_FAILED.exception(e);
        }
    }
}
