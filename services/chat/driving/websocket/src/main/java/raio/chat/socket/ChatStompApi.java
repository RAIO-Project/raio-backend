package raio.chat.socket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.dto.ChatWebSocketDto.ChatSendCommand;
import raio.socket.interceptor.StompAuthChannelInterceptor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatStompApi {

    private static final long ANONYMOUS_USER_ID = -1L;

    private final ChatSendUseCase chatSendUseCase;

    @MessageMapping("/streams/{streamId}/chat")
    public void sendChat(
            @DestinationVariable String streamId,
            @Payload @Valid ChatSendCommand command,
            SimpMessageHeaderAccessor accessor) {

        var attrs = accessor.getSessionAttributes();
        var userId = (attrs != null && attrs.get(StompAuthChannelInterceptor.USER_ID) != null)
                ? (Long) attrs.get(StompAuthChannelInterceptor.USER_ID)
                : ANONYMOUS_USER_ID;

        // 비회원은 채팅 전송 불가 (읽기만 허용). 프론트 게이팅의 서버측 강제.
        if (userId == ANONYMOUS_USER_ID) {
            log.debug("익명 사용자 채팅 전송 거부 - streamId: {}", streamId);
            return;
        }

        // 토큰에 nickname claim 이 없어 현재는 userId 를 표시명으로 사용
        // TODO(nickname): 토큰 claim 추가되면 세션의 NICKNAME 을 사용
        var nicknameAttr = attrs != null ? (String) attrs.get(StompAuthChannelInterceptor.NICKNAME) : null;
        var nickname = nicknameAttr != null ? nicknameAttr : String.valueOf(userId);

        var chatLogs = ChatLogs.builder()
                .streamId(streamId)
                .userId(String.valueOf(userId))
                .message(command.message())
                .isBlocked(false)
                .build();

        chatSendUseCase.sendMessage(chatLogs, nickname);
    }
}