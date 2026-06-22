package raio.socket.relay;

/**
 * 스트림 실시간 이벤트(채팅/입장/후원 등)의 공용 Redis 채널 + STOMP 토픽 네이밍.
 *
 * <p>도메인 무관. chat/donation 등 모든 도메인이 같은 채널(stream:events:{id})로 publish 하고,
 * 공용 {@link RelaySubscriber} 가 수신해 STOMP 토픽(/topic/streams/{id})으로 전달한다.
 */
public final class StreamRelayChannel {

    private StreamRelayChannel() {
    }

    /** Redis 채널 prefix. 패턴 구독(stream:events:*)에 사용. */
    public static final String REDIS_PREFIX = "stream:events:";
    public static final String REDIS_PATTERN = REDIS_PREFIX + "*";

    /** STOMP 클라이언트 구독 토픽 prefix. */
    public static final String STOMP_TOPIC_PREFIX = "/topic/streams/";

    public static String redisChannel(String streamId) {
        return REDIS_PREFIX + streamId;
    }

    public static String redisChannel(long streamId) {
        return REDIS_PREFIX + streamId;
    }

    /** Redis 채널명에서 streamId 추출 (stream:events:123 -> 123). */
    public static String streamIdFromChannel(String channel) {
        return channel.substring(REDIS_PREFIX.length());
    }

    public static String stompTopic(String streamId) {
        return STOMP_TOPIC_PREFIX + streamId;
    }
}