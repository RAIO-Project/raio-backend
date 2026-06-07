package raio.payment.webmvc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Page;
import raio.payment.PointHistoryReadModels.PointHistoryDetail;
import raio.payment.PointHistoryReadModels.PointHistorySummary;

public final class PointHistoryQueryDto {
    
    private PointHistoryQueryDto() {
    }
    
    @Builder
    public record PointHistorySummaryResponse(
            @Schema(description = "포인트 이력 목록")
            Page<PointHistorySummary> pointHistories
    ) {
    }
    
    @Builder
    public record PointHistoryDetailResponse(
            @Schema(description = "포인트 이력 상세")
            PointHistoryDetail pointHistory
    ) {
    }
}