package raio.stream.application.port;

import org.springframework.data.domain.Page;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;

import java.time.Instant;
import java.util.Set;

public interface StreamQueryPort {

    /**
     * 상태/카테고리/제목 검색 조건으로 스트림 목록을 조회.
     *
     * @param category 카테고리 필터. null 이면 전체 카테고리 대상.
     * @param query   제목 부분 일치 검색어. null/blank 면 검색하지 않음.
     */
    Page<LiveStreamSummary> findByStatusesAfter(
            Set<StreamStatus> statuses,
            StreamCategory category,
            String query,
            Instant lastCreatedAt,
            int size
    );
}
