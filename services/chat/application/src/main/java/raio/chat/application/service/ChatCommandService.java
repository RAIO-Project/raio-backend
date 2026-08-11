package raio.chat.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class ChatCommandService implements ChatSendUseCase, ChatBlindUseCase {

    private static final int BLACKLIST_THRESHOLD = 5;
    private static final long UNBLOCK_AFTER_DAYS = 1L;

    private final ChatCommandPort chatCommandPort;
    private final ChatQueryPort chatQueryPort;
    private final ChatBroadcastPort chatBroadcastPort;
    private final ChatModerationPort chatModerationPort;
    private final BlacklistCommandPort blacklistCommandPort;
    private final BlacklistQueryPort blacklistQueryPort;

    @Override
    public ChatLogs sendMessage(ChatLogs chatLogs, String senderNickname) {
        // TODO(정규식 1차 필터): AI 전에 금칙어/정규식으로 명백 위반 사전 차단.
        //   명백 위반이면 여기서 차단 표시 + 브로드캐스트 스킵(한순간도 안 보이게).
        //   애매한 건 통과시켜 아래 AI 비동기 모더레이션에 맡긴다.

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