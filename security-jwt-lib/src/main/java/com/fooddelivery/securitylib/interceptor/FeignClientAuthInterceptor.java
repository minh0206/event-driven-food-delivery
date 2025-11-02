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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            return;

        String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            template.header(HttpHeaders.AUTHORIZATION, authorizationHeader);
        } else {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getPrincipal().toString();
            String role = authentication.getAuthorities().stream().findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElseThrow(() -> new IllegalArgumentException("User must have at least one role"));

            String token = jwtService.generateToken(userId, role);
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
    }
}
