package raio.payment.client.toss.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientResponseException;
import raio.payment.PaymentReadModels.PaymentConfirmResult;
import raio.payment.application.port.PaymentClientPort;
import raio.payment.client.toss.dto.TossConfirmRequest;
import raio.payment.client.toss.dto.TossConfirmResponse;
import org.springframework.web.client.RestClient;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentTossClientAdapter implements PaymentClientPort {

    private final RestClient tossRestClient;

    @Override
    public PaymentConfirmResult confirm(String externalKey, String orderId, Long amount) {
        try {
            TossConfirmResponse response = tossRestClient.post()
                    .uri("/v1/payments/confirm")
                    .body(new TossConfirmRequest(externalKey, orderId, amount))
                    .retrieve()
                    .body(TossConfirmResponse.class);

            return PaymentConfirmResult.success(response.paymentKey());

        } catch (RestClientResponseException e) {
            log.warn("Toss confirm failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return PaymentConfirmResult.failure(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Toss confirm error", e);
            return PaymentConfirmResult.failure(e.getMessage());
        }
    }
}
