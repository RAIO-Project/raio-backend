package raio.payment.client.toss.dto;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
