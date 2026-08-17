package raio.wallet.adapter.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.wallet.application.usecase.PointRefundUseCase;
import raio.wallet.application.usecase.WalletCreateUseCase;
import raio.wallet.adapter.webmvc.dto.WalletCommandDto.PointAmountCommand;
import raio.wallet.adapter.webmvc.dto.WalletCommandDto.WalletCreateCommand;
import raio.wallet.adapter.webmvc.dto.WalletQueryDto.WalletResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/wallets")
@RequiredArgsConstructor
public class WalletCommandApi {
    
    private final WalletCreateUseCase walletCreateUseCase;
    private final PointRefundUseCase pointRefundUseCase;
    
    @PostMapping
    public WalletResponse create(@RequestBody WalletCreateCommand command) {
        return WalletResponse.builder()
                .wallet(walletCreateUseCase.create(command.userId()))
                .build();
    }
    
    @PostMapping("/{walletId}/refund")
    public WalletResponse refund(
            @PathVariable String walletId,
            @Valid @RequestBody PointAmountCommand command
    ) {
        return WalletResponse.builder()
                .wallet(pointRefundUseCase.refund(walletId, command.sourceId(), command.amount()))
                .build();
    }
}