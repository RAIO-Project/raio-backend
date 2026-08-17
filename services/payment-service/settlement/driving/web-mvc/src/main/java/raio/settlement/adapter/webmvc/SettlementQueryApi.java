package raio.settlement.adapter.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import raio.settlement.application.usecase.SettlementReadUseCase;
import raio.settlement.adapter.webmvc.dto.SettlementQueryDto.SettlementDetailResponse;
import raio.settlement.adapter.webmvc.dto.SettlementQueryDto.SettlementSummaryResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/settlements")
@RequiredArgsConstructor
public class SettlementQueryApi {

    private final SettlementReadUseCase settlementReadUseCase;

    @GetMapping("/{settlementId}")
    public SettlementDetailResponse getSettlement(@PathVariable String settlementId) {
        return SettlementDetailResponse.builder()
                .settlement(settlementReadUseCase.getSettlement(settlementId))
                .build();
    }

    @GetMapping("/streamers/{streamerId}")
    public SettlementSummaryResponse getSettlements(
            @PathVariable String streamerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return SettlementSummaryResponse.builder()
                .settlements(settlementReadUseCase.getSettlementsByStreamerId(
                        streamerId,
                        PageRequest.of(page, size)
                ))
                .build();
    }
}
