package raio.payment.adapter.client.toss.dto;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount,
        String method
) {
}
