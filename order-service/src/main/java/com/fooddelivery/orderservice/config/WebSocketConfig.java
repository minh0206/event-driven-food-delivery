package com.fooddelivery.orderservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The "/ws" endpoint is where the client will connect to for the WebSocket handshake.
        // withSockJS() is a fallback for browsers that don't support WebSocket.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:63342")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // These are the prefixes for messages that are bound for @MessageMapping-annotated methods.
        // We don't need this for server-to-client push, but it's good practice to have.
        registry.setApplicationDestinationPrefixes("/app");

        // Enables a simple in-memory message broker.
        // "/topic" is for public broadcasts (e.g., all users see a new restaurant).
        // "/queue" is typically used for private, user-specific messages.
        // The "/user" prefix is crucial for sending messages to a specific authenticated user.
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Register our interceptor to be executed on messages from the client.
        registration.interceptors(webSocketAuthInterceptor);
    }
}