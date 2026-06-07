package raio.payment.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.payment.application.usecase.PointChargeUseCase;
import raio.payment.application.usecase.PointRefundUseCase;
import raio.payment.application.usecase.WalletCreateUseCase;
import raio.payment.webmvc.dto.WalletCommandDto.WalletCreateCommand;
import raio.payment.webmvc.dto.WalletCommandDto.PointAmountCommand;
import raio.payment.webmvc.dto.WalletQueryDto.WalletResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/wallets")
@RequiredArgsConstructor
public class WalletCommandApi {
    
    private final WalletCreateUseCase walletCreateUseCase;
    private final PointChargeUseCase pointChargeUseCase;
    private final PointRefundUseCase pointRefundUseCase;
    
    @PostMapping
    public WalletResponse create(@RequestBody WalletCreateCommand command) {
        return WalletResponse.builder()
                .wallet(walletCreateUseCase.create(command.userId()))
                .build();
    }
    
    @PostMapping("/{walletId}/charge")
    public WalletResponse charge(
            @PathVariable String walletId,
            @Valid @RequestBody PointAmountCommand command
    ) {
        return WalletResponse.builder()
                .wallet(pointChargeUseCase.charge(walletId, command.amount()))
                .build();
    }
    
    @PostMapping("/{walletId}/refund")
    public WalletResponse refund(
            @PathVariable String walletId,
            @Valid @RequestBody PointAmountCommand command
    ) {
        return WalletResponse.builder()
                .wallet(pointRefundUseCase.refund(walletId, command.amount()))
                .build();
    }
}