package raio.payment.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import raio.payment.domain.type.PaymentMethod;
import raio.payment.domain.type.PgProvider;

public final class PaymentCommandDto {

    private PaymentCommandDto() {
    }

    public record PaymentPrepareRequest(
            @NotBlank(message = "유저 ID는 필수입니다.")
            @Schema(description = "유저 ID", example = "12345")
            String userId,

            @NotNull(message = "결제 금액은 필수입니다.")
            @Positive(message = "결제 금액은 1 이상이어야 합니다.")
            @Schema(description = "결제 금액 (KRW)", example = "10000")
            Long amount,

            @NotNull(message = "결제 수단은 필수입니다.")
            @Schema(description = "결제 수단", example = "EASY_PAY")
            PaymentMethod method,
            
            @Schema(description = "PG사", example = "TOSS")
            PgProvider pgProvider
    ) {
    }

    @Builder
    public record PreparePaymentResponse(
            @Schema(description = "결제 식별자")
            String paymentId,

            @Schema(description = "가맹점 주문번호")
            String orderId,

            @Schema(description = "결제 금액")
            Long amount
    ) {
    }

    public record PaymentConfirmRequest(
            @NotBlank(message = "결제 식별자는 필수입니다.")
            @Schema(description = "결제 식별자")
            String paymentId,

            @NotBlank(message = "PG사 결제 키는 필수입니다.")
            @Schema(description = "PG사 결제 키 (paymentKey)", example = "5zJ4xY7m0kODnyRpQWGrN2eqe...")
            String paymentKey,

            @NotBlank(message = "주문번호는 필수입니다.")
            @Schema(description = "가맹점 주문번호")
            String orderId,

            @NotNull(message = "결제 금액은 필수입니다.")
            @Positive(message = "결제 금액은 1 이상이어야 합니다.")
            @Schema(description = "결제 금액")
            Long amount
    ) {
    }

    @Builder
    public record ConfirmPaymentResponse(
            @Schema(description = "결제 식별자")
            String paymentId,

            @Schema(description = "가맹점 주문번호")
            String orderId,

            @Schema(description = "결제 상태")
            String status
    ) {
    }
}
