package raio.payment.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

public final class WalletCommandDto {
    
    private WalletCommandDto() {
    }
    
    @Builder
    public record WalletCreateCommand(
            @NotNull(message = "유저 ID는 필수입니다.")
            @Schema(description = "유저 ID", example = "1")
            String userId
    ) {
    }
    
    @Builder
    public record PointAmountCommand(
            @NotNull(message = "포인트 금액은 필수입니다.")
            @Positive(message = "포인트 금액은 1 이상이어야 합니다.")
            @Schema(description = "포인트 금액", example = "10000")
            Long amount
    ) {
    }
}