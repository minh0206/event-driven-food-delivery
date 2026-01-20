package com.fooddelivery.securitylib.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fooddelivery.securitylib.service.JwtService;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FeignClientAuthInterceptor implements RequestInterceptor {
    private final JwtService jwtService;

    @Override
    public void apply(RequestTemplate template) {
        // Skip authentication for internal endpoints
        // if (template.path().contains("/internal/")) {
        // return;
        // }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return;

        String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            template.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            // Skip if anonymous user
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return;
            }

            String userId = authentication.getPrincipal().toString();
            String role = authentication.getAuthorities().stream().findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new IllegalArgumentException("User must have at least one role"));

            String token = jwtService.generateAccessToken(userId, role);
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
    }
}
