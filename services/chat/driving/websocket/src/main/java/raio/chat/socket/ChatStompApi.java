package raio.chat.socket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.dto.ChatWebSocketDto.ChatSendCommand;
import raio.socket.interceptor.StompPrincipal;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatStompApi {

    private final ChatSendUseCase chatSendUseCase;

    @MessageMapping("/streams/{streamId}/chat")
    public void sendChat(
            @DestinationVariable String streamId,
            @Payload @Valid ChatSendCommand command,
            Principal principal) {

        // 비회원은 전송 거부.
        if (!(principal instanceof StompPrincipal user)) {
            log.debug("익명 사용자 채팅 전송 거부 - streamId: {}", streamId);
            return;
        }

        Long userId = Long.parseLong(user.getName());
        String nickname = user.nickname();

        var chatLogs = ChatLogs.builder()
                .streamId(streamId)
                .userId(String.valueOf(userId))
                .message(command.message())
                .isBlocked(false)
                .build();

        chatSendUseCase.sendMessage(chatLogs, nickname);
    }
}