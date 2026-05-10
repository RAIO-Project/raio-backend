package raio.chat.socket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import raio.chat.application.usecase.ChatSendUseCase;
import raio.chat.domain.ChatLogs;
import raio.chat.socket.dto.ChatWebSocketDto.ChatSendCommand;
import raio.chat.socket.interceptor.StompAuthInterceptor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompApi {

    private final ChatSendUseCase chatSendUseCase;

    @MessageMapping("/streams/{streamId}/chat")
    public void sendChat(
            @DestinationVariable String streamId,
            @Payload @Valid ChatSendCommand command,
            SimpMessageHeaderAccessor accessor) {

        var attrs    = accessor.getSessionAttributes();
        var userId = (attrs != null && attrs.get(StompAuthInterceptor.USER_ID) != null)
                ? (Long) attrs.get(StompAuthInterceptor.USER_ID)
                : -1L;
        var nickname = attrs != null ? (String) attrs.get(StompAuthInterceptor.NICKNAME) : "anonymous";

        var chatLogs = ChatLogs.builder()
                .streamId(streamId)
                .userId(String.valueOf(userId))
                .message(command.message())
                .isBlocked(false)
                .build();

        chatSendUseCase.sendMessage(chatLogs, nickname); // ← nickname 전달
    }
}