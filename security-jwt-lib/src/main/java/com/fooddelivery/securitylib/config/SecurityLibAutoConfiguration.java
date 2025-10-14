package com.fooddelivery.securitylib.config;

import com.fooddelivery.securitylib.filter.JwtAuthenticationFilter;
import com.fooddelivery.securitylib.service.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication // This configuration will only be active in a web application context
public class SecurityLibAutoConfiguration {

    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }
}