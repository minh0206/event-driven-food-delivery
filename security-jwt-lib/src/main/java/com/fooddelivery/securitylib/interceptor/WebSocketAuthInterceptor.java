package com.fooddelivery.securitylib.interceptor;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fooddelivery.securitylib.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // Check if it's a CONNECT command
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand()))
            return message;

        // Get the "Authorization" header from the STOMP frame
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return message;

        String jwt = authHeader.substring(7);
        try {
            // This will throw an exception if the token is expired or invalid
            jwtService.validateToken(jwt);
        } catch (JwtException e) {
            // resolver.resolveException(request, response, null, e);
            log.error(e.getMessage());
            return message;
        }

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

        return message;
    }
}