package raio.chat.socket.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import raio.chat.application.port.ViewerPort;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link ViewerPort} 의 Redis 구현.
 *
 * 키 구조:
 *  - viewers:{streamId}        : 시청 중인 sessionId 들의 Set (SCARD = 인원수)
 *  - viewer:session:{sessionId}: 그 세션이 보는 streamId (퇴장 시 역조회용)
 *  - active:streams            : 시청자 있는 streamId 들의 Set (스케줄러 순회용)
 */
@Component
@RequiredArgsConstructor
public class ViewerRedisAdapter implements ViewerPort {

    private static final String VIEWERS = "viewers:";          // + streamId
    private static final String SESSION = "viewer:session:";   // + sessionId
    private static final String ACTIVE = "active:streams";

    private final StringRedisTemplate redis;

    @Override
    public void join(Long streamId, String sessionId) {
        redis.opsForSet().add(VIEWERS + streamId, sessionId);    // 시청자 집합에 추가
        redis.opsForValue().set(SESSION + sessionId, String.valueOf(streamId)); // 역매핑 저장
        redis.opsForSet().add(ACTIVE, String.valueOf(streamId)); // 활성 방송 표시
    }

    @Override
    public void leave(String sessionId) {
        String key = SESSION + sessionId;
        String streamId = redis.opsForValue().get(key);
        if (streamId == null) return; // 추적 안 되던 세션(스트림 토픽 구독 안 함)이면 무시

        redis.opsForSet().remove(VIEWERS + streamId, sessionId); // 집합에서 제거
        redis.delete(key);                                       // 역매핑 삭제
        // 시청자 0명이면 활성 목록에서 정리 (스케줄러가 빈 방송 안 돌게)
        Long size = redis.opsForSet().size(VIEWERS + streamId);
        if (size == null || size == 0) {
            redis.opsForSet().remove(ACTIVE, streamId);
        }
    }

    @Override
    public long count(Long streamId) {
        Long size = redis.opsForSet().size(VIEWERS + streamId);
        return size == null ? 0 : size;
    }

    @Override
    public Set<Long> activeStreamIds() {
        Set<String> members = redis.opsForSet().members(ACTIVE);
        if (members == null) return Collections.emptySet();
        return members.stream().map(Long::valueOf).collect(Collectors.toSet());
    }
}