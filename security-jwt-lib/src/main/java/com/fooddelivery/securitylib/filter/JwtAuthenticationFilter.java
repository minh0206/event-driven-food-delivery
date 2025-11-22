package com.fooddelivery.securitylib.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.securitylib.service.TokenValidationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenValidationService tokenValidationService;

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtService jwtService, TokenValidationService tokenValidationService,
            HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.tokenValidationService = tokenValidationService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Skip JWT validation for refresh endpoint (it uses refresh token cookie, not
        // access token)
        String requestPath = request.getRequestURI();
        if (requestPath.endsWith("/refresh") || requestPath.endsWith("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        try {
            // This will throw an exception if the token is expired or invalid
            jwtService.validateToken(jwt);

            // Check if token is revoked in database
            if (tokenValidationService != null && !tokenValidationService.isTokenValid(jwt)) {
                log.error("Token has been revoked");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (JwtException e) {
            resolver.resolveException(request, response, null, e);
            log.error(e.getMessage());
            return;
        }

        Claims claims = jwtService.extractAllClaims(jwt);
        // Extract the role claim and convert it to a collection of GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("role", String.class)));

        var authToken = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                jwt,
                authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set it in the Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}