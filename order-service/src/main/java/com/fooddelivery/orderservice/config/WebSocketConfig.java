package com.fooddelivery.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fooddelivery.securitylib.interceptor.WebSocketAuthInterceptor;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${app.cors.allowed-origins:*}")
    private @NonNull String[] allowedOrigins;

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // The "/ws" endpoint is where the client will connect to for the WebSocket
        // handshake.
        // withSockJS() is a fallback for browsers that don't support WebSocket.
        registry.addEndpoint("/ws/order")
                .setAllowedOriginPatterns(allowedOrigins)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        // Enables a simple in-memory message broker.
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(@NonNull ChannelRegistration registration) {
        // Register our interceptor to be executed on messages from the client.
        registration.interceptors(webSocketAuthInterceptor);
    }
}