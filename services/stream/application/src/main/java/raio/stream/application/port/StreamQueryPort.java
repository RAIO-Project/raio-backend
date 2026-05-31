package raio.stream.application.port;

import org.springframework.data.domain.Page;
import raio.stream.domain.type.StreamCategory;
import raio.stream.domain.type.StreamStatus;
import raio.stream.readmodel.StreamQueryModels.LiveStreamSummary;
import raio.stream.readmodel.StreamQueryModels.StreamDetail;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 방송 조회 포트 (driven).
 */
public interface StreamQueryPort {

    /**
     * [최신순] 상태/카테고리/제목 검색 조건으로 키셋 페이징 조회.
     *
     * @param category 카테고리 필터. null 이면 전체.
     * @param query    제목 부분 일치 검색어. null/blank 면 검색하지 않음.
     */
    Page<LiveStreamSummary> findByStatusesAfter(
            Set<StreamStatus> statuses,
            StreamCategory category,
            String query,
            Instant lastCreatedAt,
            int size
    );

    /**
     * [시청자순] Redis 랭킹에서 받은 ID 집합을 LIVE 로 hydration.
     * 순서/페이징은 호출자가 Redis 순서 기준으로 처리하므로 정렬하지 않는다.
     *
     * @param category 카테고리 필터. null 이면 전체.
     * @param query    제목 부분 일치 검색어. null/blank 면 검색하지 않음.
     */
    List<LiveStreamSummary> findLiveSummariesByIds(
            Collection<Long> ids,
            StreamCategory category,
            String query
    );

    /** 단건 상세 조회. 소켓 연결 전 LIVE 검증 + 메타 제공. */
    Optional<StreamDetail> findDetailById(String id);
}