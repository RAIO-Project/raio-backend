package raio.settlement.adapter.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import raio.settlement.application.usecase.SettlementSettingReadUseCase;
import raio.settlement.adapter.webmvc.dto.SettlementSettingQueryDto.SettlementSettingDueResponse;
import raio.settlement.adapter.webmvc.dto.SettlementSettingQueryDto.SettlementSettingResponse;

import java.time.Instant;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/settlement-settings")
@RequiredArgsConstructor
public class SettlementSettingQueryApi {

    private final SettlementSettingReadUseCase settlementSettingReadUseCase;

    @GetMapping("/streamers/{streamerId}")
    public SettlementSettingResponse getSettlementSetting(@PathVariable String streamerId) {
        return SettlementSettingResponse.builder()
                .settlementSetting(settlementSettingReadUseCase.getSettlementSetting(streamerId))
                .build();
    }

    @GetMapping("/due")
    public SettlementSettingDueResponse getDueSettlementSettings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant now,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Instant referenceAt = now != null ? now : Instant.now();

        return SettlementSettingDueResponse.builder()
                .settlementSettings(settlementSettingReadUseCase.getSettlementDueSettings(
                        referenceAt,
                        PageRequest.of(page, size)
                ))
                .build();
    }
}
