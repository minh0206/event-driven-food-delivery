package com.fooddelivery.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class GlobalCorsConfig implements WebMvcConfigurer {
    @Value("${app.cors.allowed-origins:*}")
    private @NonNull String[] allowedOrigins = {};

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        log.info("Allowed origins: {}", (Object) allowedOrigins);
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}