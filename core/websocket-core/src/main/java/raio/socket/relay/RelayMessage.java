package raio.socket.relay;

import java.time.Instant;

/**
 * WebSocket/Redis 로 인스턴스 간 전달되는 실시간 메시지의 공통 계약.
 *
 * <p>core 는 어떤 도메인 메시지가 있는지 모른다(열린 인터페이스). 각 도메인(chat, donation 등)이
 * 자기 모듈에서 이 계약을 구현하는 record 를 정의한다. 새 타입 추가 시 core 를 건드리지 않는다.
 *
 * <p>{@link #type()} 는 메시지 종류 식별자(예: "CHAT", "DONATION", "JOIN", "LEAVE").
 * 구독자는 보통 자기 도메인 타입만 역직렬화/처리하므로 sealed 망라 분기는 두지 않는다.
 */
public interface RelayMessage {
    String type();
    String streamId();
    Instant occurredAt();
}