package raio.chat.rdb;

/** 블랙리스트 조회 캐시의 Redis 키·값 규약. 차단/비차단을 모두 캐싱해 채팅마다 DB를 보지 않는다. */
final class BlacklistCache {

    private static final String PREFIX = "blacklist:active:";

    static final String BLOCKED = "1";
    static final String NOT_BLOCKED = "0";

    private BlacklistCache() {
    }

    static String key(String userId) {
        return PREFIX + userId;
    }
}
