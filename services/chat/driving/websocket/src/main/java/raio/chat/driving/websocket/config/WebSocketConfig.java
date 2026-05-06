package raio.chat.driving.websocket.config;

import lombok.RequiredArgsConstructor;
import raio.chat.driving.websocket.interceptor.StompAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthInterceptor stompAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
//                .addInterceptors(stompAuthInterceptor)
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");   // 구독 채널 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 발송 prefix
    }
}