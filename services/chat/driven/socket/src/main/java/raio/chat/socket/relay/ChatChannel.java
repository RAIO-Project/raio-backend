package raio.chat.socket.relay;

public final class ChatChannel {

    private ChatChannel() {
    }

    /** Redis 채널 prefix. 패턴 구독(chat:stream:*)에 사용. */
    public static final String REDIS_PREFIX = "chat:stream:";
    public static final String REDIS_PATTERN = REDIS_PREFIX + "*";

    /** STOMP 클라이언트 구독 토픽 prefix. */
    public static final String STOMP_TOPIC_PREFIX = "/topic/streams/";

    public static String redisChannel(String streamId) {
        return REDIS_PREFIX + streamId;
    }

    public static String redisChannel(long streamId) {
        return REDIS_PREFIX + streamId;
    }

    /** Redis 채널명에서 streamId 추출 (chat:stream:123 -> 123). */
    public static String streamIdFromChannel(String channel) {
        return channel.substring(REDIS_PREFIX.length());
    }

    public static String stompTopic(String streamId) {
        return STOMP_TOPIC_PREFIX + streamId;
    }
}