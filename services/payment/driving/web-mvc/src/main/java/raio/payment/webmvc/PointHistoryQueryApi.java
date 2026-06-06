package raio.payment.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import raio.payment.application.usecase.PointHistoryReadUseCase;
import raio.payment.webmvc.dto.PointHistoryQueryDto.PointHistoryDetailResponse;
import raio.payment.webmvc.dto.PointHistoryQueryDto.PointHistorySummaryResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/point-histories")
@RequiredArgsConstructor
public class PointHistoryQueryApi {
    
    private final PointHistoryReadUseCase pointHistoryReadUseCase;
    
    @GetMapping("/wallets/{walletId}")
    public PointHistorySummaryResponse getPointHistories(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return PointHistorySummaryResponse.builder()
                .pointHistories(pointHistoryReadUseCase.getPointHistorySummary(
                        walletId,
                        PageRequest.of(page, size)
                ))
                .build();
    }
    
    @GetMapping("/{pointHistoryId}")
    public PointHistoryDetailResponse getPointHistory(@PathVariable String pointHistoryId) {
        return PointHistoryDetailResponse.builder()
                .pointHistory(pointHistoryReadUseCase.getPointHistoryDetail(pointHistoryId))
                .build();
    }
}