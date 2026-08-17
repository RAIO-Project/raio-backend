package raio.settlement.adapter.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

public final class SettlementSettingQueryDto {

    private SettlementSettingQueryDto() {
    }

    @Builder
    public record SettlementSettingResponse(
            @Schema(description = "정산 설정")
            SettlementSettingSummary settlementSetting
    ) {
    }

    @Builder
    public record SettlementSettingDueResponse(
            @Schema(description = "정산 실행 대상 정산 설정 목록")
            Page<SettlementSettingSummary> settlementSettings
    ) {
    }
}
