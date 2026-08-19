package raio.wallet.adapter.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import raio.wallet.domain.Wallet;

public final class WalletQueryDto {
    
    private WalletQueryDto() {
    }
    
    @Builder
    public record WalletResponse(
            @Schema(description = "지갑 정보")
            Wallet wallet
    ) {
    }
}