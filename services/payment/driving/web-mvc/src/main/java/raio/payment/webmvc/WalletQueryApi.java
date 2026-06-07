package raio.payment.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import raio.payment.application.usecase.WalletReadUseCase;
import raio.payment.webmvc.dto.WalletQueryDto.WalletResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/wallets")
@RequiredArgsConstructor
public class WalletQueryApi {
    
    private final WalletReadUseCase walletReadUseCase;
    
    @GetMapping("/{userId}")
    public WalletResponse getWallet(@PathVariable String userId) {
        return WalletResponse.builder()
                .wallet(walletReadUseCase.getWallet(userId))
                .build();
    }
}