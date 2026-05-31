package raio.stream.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import raio.stream.application.port.StreamLiveRankPort;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * {@link StreamLiveRankPort} 의 Redis ZSET 구현.
 * core 의 {@code :redis-template} 가 제공하는 {@link StringRedisTemplate} 사용.
 * member = streamId, score = 현재 시청자 수. key = {@value #LIVE_VIEWERS_KEY}
 */
@Repository
public class StreamRankRedisAdapter implements StreamLiveRankPort {

    private static final String LIVE_VIEWERS_KEY = "stream:live:viewers";

    private final StringRedisTemplate redisTemplate;

    public StreamRankRedisAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<LiveRank> findLiveRanksByViewerDesc() {
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(LIVE_VIEWERS_KEY, 0, -1);

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }
        return tuples.stream()
                .filter(t -> t.getValue() != null)
                .sorted(Comparator
                        .comparingDouble((ZSetOperations.TypedTuple<String> t) ->
                                t.getScore() == null ? 0.0 : t.getScore())
                        .reversed()
                        .thenComparing(t -> Long.parseLong(t.getValue()), Comparator.reverseOrder()))
                .map(t -> new LiveRank(
                        Long.parseLong(t.getValue()),
                        t.getScore() == null ? 0L : t.getScore().longValue()))
                .toList();
    }

    @Override
    public long currentViewerCount(long streamId) {
        Double score = redisTemplate.opsForZSet().score(LIVE_VIEWERS_KEY, String.valueOf(streamId));
        return score == null ? 0L : score.longValue();
    }

    @Override
    public void addLiveStream(long streamId) {
        redisTemplate.opsForZSet().addIfAbsent(LIVE_VIEWERS_KEY, String.valueOf(streamId), 0.0);
    }

    @Override
    public void removeLiveStream(long streamId) {
        redisTemplate.opsForZSet().remove(LIVE_VIEWERS_KEY, String.valueOf(streamId));
    }

    @Override
    public long increaseViewerCount(long streamId) {
        Double v = redisTemplate.opsForZSet().incrementScore(LIVE_VIEWERS_KEY, String.valueOf(streamId), 1.0);
        return v == null ? 0L : v.longValue();
    }

    @Override
    public long decreaseViewerCount(long streamId) {
        Double v = redisTemplate.opsForZSet().incrementScore(LIVE_VIEWERS_KEY, String.valueOf(streamId), -1.0);
        return v == null ? 0L : v.longValue();
    }
}
