package com.fooddelivery.securitylib.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fooddelivery.securitylib.interceptor.FeignClientAuthInterceptor;
import com.fooddelivery.securitylib.service.JwtService;

import feign.Logger;
import feign.Request;

@Configuration
public class FeignClientConfiguration {
    @Bean
    FeignClientAuthInterceptor feignClientAuthInterceptor(JwtService jwtService) {
        return new FeignClientAuthInterceptor(jwtService);
    }

    // Other potential configurations:

    // Configure logging level
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Logs headers, body, and metadata
    }

    // Configure timeouts (optional, can also use properties)
    @Bean
    Request.Options options() {
        // Connect timeout in seconds, Read timeout in seconds
        return new Request.Options(50, TimeUnit.SECONDS, 50, TimeUnit.SECONDS, true);
    }
}
