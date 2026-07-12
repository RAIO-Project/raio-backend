package raio.stream.web.dto;

import raio.stream.domain.type.StreamCategory;

public final class StreamLifecycleDto {
    private StreamLifecycleDto() {
    }

    /**
     * category 는 enum 이름 문자열로 받는다 (예: "GAMING"). Spring 이 StreamCategory 로 역직렬화.
     */
    public record OpenStreamRequest(
            String title,
            StreamCategory category
    ) {
    }
}
