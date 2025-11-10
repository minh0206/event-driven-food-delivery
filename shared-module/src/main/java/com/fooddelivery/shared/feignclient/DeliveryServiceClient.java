package com.fooddelivery.shared.feignclient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;

@FeignClient(name = "delivery-service", configuration = FeignClientConfiguration.class)
public interface DeliveryServiceClient {
    @PostMapping(path = "/internal/drivers", consumes = "application/json", produces = "application/json")
    Map<String, Long> createDriver();
}
