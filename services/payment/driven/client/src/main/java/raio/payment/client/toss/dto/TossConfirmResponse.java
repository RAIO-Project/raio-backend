package raio.payment.client.toss.dto;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount,
        String method
) {
}
