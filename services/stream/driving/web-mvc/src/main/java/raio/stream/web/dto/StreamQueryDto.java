package raio.stream.web.dto;

/**
 * stream Query API 의 요청/응답 DTO 모음.
 *
 * <p>샘플 {@code ArticleQueryDto} 컨벤션 적용:
 *  - {@code final class} + private 생성자 + inner record.
 *
 * <p>현재 KAN-24 의 라이브 목록 조회는 readmodel({@code Page<LiveStreamSummary>}) 을
 * 컨트롤러에서 직접 반환하므로 응답 record 가 필요하지 않다.
 * 단건 조회 등이 추가될 때 inner record 를 채울 자리.
 */
public final class StreamQueryDto {
    private StreamQueryDto() {
    }
}
