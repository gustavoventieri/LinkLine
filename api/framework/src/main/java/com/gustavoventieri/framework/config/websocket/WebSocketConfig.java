package com.gustavoventieri.framework.config.websocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo para os destinos que o broker vai usar para enviar mensagens para clientes
        config.enableSimpleBroker("/topic", "/queue");
        // Prefixo para mensagens enviadas do cliente para o servidor (controller)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexão WebSocket, com suporte a fallback (SockJS)
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
    }
}
