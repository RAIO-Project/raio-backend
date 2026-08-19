package raio.settlement.adapter.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;
import raio.settlement.readmodel.SettlementReadModels.SettlementDetail;

public final class SettlementQueryDto {

    private SettlementQueryDto() {
    }

    @Builder
    public record SettlementDetailResponse(
            @Schema(description = "정산 상세")
            SettlementDetail settlement
    ) {
    }

    @Builder
    public record SettlementSummaryResponse(
            @Schema(description = "정산 목록")
            Page<SettlementDetail> settlements
    ) {
    }
}
