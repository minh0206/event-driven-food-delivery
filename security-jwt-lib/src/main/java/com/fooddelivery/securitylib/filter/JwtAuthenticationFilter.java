package com.fooddelivery.securitylib.filter;

import com.fooddelivery.securitylib.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        if (SecurityContextHolder.getContext().getAuthentication() == null && jwtService.validateToken(jwt)) {
            // If token is valid, create Authentication object from it
            Claims claims = jwtService.extractAllClaims(jwt);

            // Extract the role claim and convert it to a collection of GrantedAuthority
            Collection<? extends GrantedAuthority> authorities =
                    Collections.singletonList(
                            new SimpleGrantedAuthority(claims.get("role", String.class))
                    );
            UserDetails principal = new User(claims.getSubject(), "", authorities);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set it in the Spring Security Context
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }
}