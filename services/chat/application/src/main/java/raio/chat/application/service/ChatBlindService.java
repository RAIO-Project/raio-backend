package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.application.usecase.ChatBlindUseCase;


/**
 * 채팅 블라인드 처리.
 * 1) chat_logs.is_blocked 갱신 (영속 — 나중에 입장한 시청자도 안 보이게)
 * 2) BLIND 이벤트 브로드캐스트 (실시간 — 현재 시청자 화면에서 가림)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBlindService implements ChatBlindUseCase {

    private final ChatCommandPort chatCommandPort;
    private final ChatBroadcastPort chatBroadcastPort;

    @Transactional
    @Override
    public void blind(String chatId, Long streamId, String reason) {
        chatCommandPort.markBlocked(chatId, reason);
        chatBroadcastPort.broadcastBlind(streamId, chatId, reason);
        log.debug("채팅 블라인드 - chatId: {}, streamId: {}, reason: {}", chatId, streamId, reason);
    }
}