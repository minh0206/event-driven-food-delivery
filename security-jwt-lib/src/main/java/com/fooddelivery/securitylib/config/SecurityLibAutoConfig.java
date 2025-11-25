package com.fooddelivery.securitylib.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fooddelivery.securitylib.filter.JwtAuthenticationFilter;
import com.fooddelivery.securitylib.interceptor.WebSocketAuthInterceptor;
import com.fooddelivery.securitylib.service.JwtService;

@Configuration
@ConditionalOnWebApplication // This configuration will only be active in a web application context
public class SecurityLibAutoConfig {
    @Bean
    JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    @Bean
    JwtService jwtService(JwtConfig jwtConfig) {
        return new JwtService(jwtConfig);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
            @Autowired(required = false) com.fooddelivery.securitylib.service.TokenValidationService tokenValidationService) {
        return new JwtAuthenticationFilter(jwtService, tokenValidationService, resolver);
    }

    @Bean
    WebSocketAuthInterceptor webSocketAuthInterceptor(
            JwtService jwtService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return new WebSocketAuthInterceptor(jwtService, resolver);
    }
}