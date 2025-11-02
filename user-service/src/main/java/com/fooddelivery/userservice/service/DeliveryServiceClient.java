package com.fooddelivery.userservice.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;

@FeignClient(name = "delivery-service", url = "${delivery-service.url}", configuration = FeignClientConfiguration.class)
public interface DeliveryServiceClient {
    @PostMapping(consumes = "application/json", produces = "application/json")
    Map<String, Long> createDriver();
}
