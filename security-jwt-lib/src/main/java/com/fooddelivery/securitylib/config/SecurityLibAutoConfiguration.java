package com.fooddelivery.securitylib.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fooddelivery.securitylib.filter.JwtAuthenticationFilter;
import com.fooddelivery.securitylib.interceptor.WebSocketAuthInterceptor;
import com.fooddelivery.securitylib.service.JwtService;

@Configuration
@ConditionalOnWebApplication // This configuration will only be active in a web application context
public class SecurityLibAutoConfiguration {

    @Bean
    JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    WebSocketAuthInterceptor webSocketAuthInterceptor(JwtService jwtService) {
        return new WebSocketAuthInterceptor(jwtService);
    }
}