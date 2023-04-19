package com.umbrella.Config.SocketConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("ws-stomp")
                .withSockJS(); // 여러가지 End Points 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic/"); // 메모리 기반 메시지 브로커가 api 구독하고 있는 클라이언트에게 메세지 전달
        registry.setApplicationDestinationPrefixes("/app"); // 서버에서 클라이언트로부터의 메세지를 받을 api 의 prefix
    }

}
