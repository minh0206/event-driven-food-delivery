package com.fooddelivery.securitylib.interceptor;

import java.util.Collections;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fooddelivery.securitylib.service.JwtService;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
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
                            Collections.singleton(new SimpleGrantedAuthority(role)));
                    // Associate the Principal with the WebSocket session
                    accessor.setUser(user);
                }
            }
        }
        return message;
    }
}