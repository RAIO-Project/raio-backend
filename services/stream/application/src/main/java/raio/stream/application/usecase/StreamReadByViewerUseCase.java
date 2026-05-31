package raio.stream.application.usecase;

import org.springframework.data.domain.Page;
import raio.stream.domain.type.StreamCategory;
import raio.stream.readmodel.StreamQueryModels.LiveStreamRankItem;

/** [시청자순] 실시간 시청자 수 기준 라이브 목록 조회 (Redis 랭킹 + offset 페이징). */
public interface StreamReadByViewerUseCase {
    Page<LiveStreamRankItem> findByViewer(
            StreamCategory category,
            String query,
            int offset,
            int size
    );
}
