package raio.settlement.adapter.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import raio.settlement.domain.type.SettlementCycle;

import java.time.Instant;

public final class SettlementSettingCommandDto {

    private SettlementSettingCommandDto() {
    }

    @Builder
    public record CycleChangeRequest(
            @NotNull(message = "변경할 정산 주기는 필수입니다.")
            @Schema(description = "변경할 정산 주기", example = "WEEKLY")
            SettlementCycle newCycle,

            @NotNull(message = "적용 시각은 필수입니다.")
            @Schema(description = "정산 주기 적용 시각")
            Instant effectiveAt
    ) {
    }
}
