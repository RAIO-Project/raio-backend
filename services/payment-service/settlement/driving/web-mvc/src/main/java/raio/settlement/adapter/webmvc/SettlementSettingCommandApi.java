package raio.settlement.adapter.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import raio.settlement.application.command.SettlementCommands.SettlementCycleChangeCommand;
import raio.settlement.application.usecase.SettlementSettingUpdateUseCase;
import raio.settlement.adapter.webmvc.dto.SettlementSettingCommandDto.CycleChangeRequest;
import raio.settlement.adapter.webmvc.dto.SettlementSettingQueryDto.SettlementSettingResponse;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequestMapping("/payment/settlement-settings")
@RequiredArgsConstructor
public class SettlementSettingCommandApi {

    private final SettlementSettingUpdateUseCase settlementSettingUpdateUseCase;

    @PostMapping("/streamers/{streamerId}/cycle-change")
    public SettlementSettingResponse changeCycle(
            @PathVariable String streamerId,
            @Valid @RequestBody CycleChangeRequest request
    ) {
        return SettlementSettingResponse.builder()
                .settlementSetting(settlementSettingUpdateUseCase.changeCycle(
                        new SettlementCycleChangeCommand(streamerId, request.newCycle(), request.effectiveAt())
                ))
                .build();
    }

    @DeleteMapping("/streamers/{streamerId}/cycle-change")
    public SettlementSettingResponse cancelCycleChange(@PathVariable String streamerId) {
        return SettlementSettingResponse.builder()
                .settlementSetting(settlementSettingUpdateUseCase.cancelCycleChange(streamerId))
                .build();
    }
}
