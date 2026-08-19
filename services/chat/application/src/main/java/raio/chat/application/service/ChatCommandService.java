package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raio.chat.application.filter.ChatProfanityFilter;
import raio.chat.application.port.BlacklistCommandPort;
import raio.chat.application.port.BlacklistQueryPort;
import raio.chat.application.port.ChatBroadcastPort;
import raio.chat.application.port.ChatCommandPort;
import raio.chat.application.port.ChatModerationPort;
import raio.chat.application.port.ChatQueryPort;
import raio.chat.application.usecase.ChatBlindUseCase;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.Blacklist;
import raio.chat.domain.ChatLogs;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatSendUseCase, ChatBlindUseCase {

    private static final int BLACKLIST_THRESHOLD = 5;
    private static final long UNBLOCK_AFTER_DAYS = 1L;

    private static final String PROFANITY_FILTER_REASON = "PROFANITY_FILTER";

    private final ChatCommandPort chatCommandPort;
    private final ChatQueryPort chatQueryPort;
    private final ChatBroadcastPort chatBroadcastPort;
    private final ChatModerationPort chatModerationPort;
    private final BlacklistCommandPort blacklistCommandPort;
    private final BlacklistQueryPort blacklistQueryPort;
    private final ChatProfanityFilter chatProfanityFilter;

    @Override
    public ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname) {
        // 0. 블랙리스트(활성 차단) 사용자는 전송 자체를 거부
        if (blacklistQueryPort.existsActiveByUserId(chatLogs.getUserId())) {
            log.debug("블랙리스트 사용자 채팅 전송 거부 - userId: {}", chatLogs.getUserId());
            return null;
        }

        // 1. 정규식 1차 필터 — 명백한 금칙어는 여기서 처리
        if (chatProfanityFilter.containsProfanity(chatLogs.getMessage())) {
            return saveBlocked(chatLogs, senderNickname);
        }

        // 1. DB 저장
        var saved = chatCommandPort.save(chatLogs, senderNickname);

        // 2. WebSocket broadcast (즉시 표시)
        Long streamId = saved.getStreamId() != null ? Long.parseLong(saved.getStreamId()) : null;
        chatBroadcastPort.broadcastMessage(streamId, saved, senderNickname);

        // 3. AI 모더레이션 큐 적재 (비동기 — 사후 블라인드).
        if (saved.getId() != null && streamId != null) {
            chatModerationPort.enqueue(saved.getId(), saved.getStreamId(), saved.getMessage());
        }
        return saved;
    }

    /**
     * 금칙어 채팅을 차단 상태로 저장한다.
     *
     * <p>저장 후 UPDATE 로 차단 표시하는 대신 저장 시점에 차단 상태를 함께 기록한다. DB 쓰기가
     * INSERT 한 번으로 끝나고, 저장과 차단 사이에 이 채팅이 노출될 여지도 없다.
     *
     * <p>누적 위반 집계 및 블랙리스트 처리X.
     * 정규식 차단까지 집계할지는 이후 별도로 정한다.
     */
    private ChatLogs saveBlocked(ChatLogs chatLogs, String senderNickname) {
        // 차단 여부와 사유를 함께 설정하는 책임은 도메인이 진다.
        chatLogs.blind(PROFANITY_FILTER_REASON);

        var saved = chatCommandPort.save(chatLogs, senderNickname);

        log.debug("채팅 사전 차단(정규식) - chatId: {}, streamId: {}, userId: {}",
                saved.getId(), saved.getStreamId(), saved.getUserId());

        return saved;
    }

    /**
     * 블라인드 영속 갱신 (is_blocked=true) + 누적 위반 5회 이상이면 블랙리스트 처리.
     * 실시간 통지는 컨슈머에서 커밋 후 별도로 수행.
     */
    @Transactional
    @Override
    public boolean markBlocked(String chatId, String reason) {
        chatCommandPort.markBlocked(chatId, reason);

        String userId = chatQueryPort.findUserIdById(chatId);

        if (blacklistQueryPort.existsActiveByUserId(userId)) {
            return false; // 이미 활성 차단 중 — 중복 블랙리스트 처리 방지
        }

        long blockedCount = chatQueryPort.countBlockedByUserId(userId);

        if (blockedCount >= BLACKLIST_THRESHOLD) {
            blacklistCommandPort.save(Blacklist.builder()
                    .userId(userId)
                    .reason(reason)
                    .unblockAt(Instant.now().plus(UNBLOCK_AFTER_DAYS, ChronoUnit.DAYS))
                    .build());
            return true;
        }
        return false;
    }
}