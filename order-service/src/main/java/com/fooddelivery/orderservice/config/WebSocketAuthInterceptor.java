package com.fooddelivery.orderservice.config;

import com.fooddelivery.securitylib.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Check if it's a CONNECT command
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Get the "Authorization" header from the STOMP frame
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwt = authHeader.substring(7);

                if (jwtService.validateToken(jwt)) {
                    Claims claims = jwtService.extractAllClaims(jwt);
                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);
                    // Create a Spring Security Principal
                    UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                            userId, // This becomes the Principal's name
                            jwt,
                            Collections.singleton(new SimpleGrantedAuthority(role))
                    );
                    // Associate the Principal with the WebSocket session
                    accessor.setUser(user);
                }
            }
        }
        return message;
    }
}