package raio.stream.application.port;

/** 방송 실시간 이벤트 브로드캐스트 (driven). 공용 relay 채널로 발행된다. */
public interface StreamBroadcastPort {

    /** 현재 시청자 수를 방 구독자에게 전달. */
    void broadcastViewerCount(String streamId, long viewerCount);
}
