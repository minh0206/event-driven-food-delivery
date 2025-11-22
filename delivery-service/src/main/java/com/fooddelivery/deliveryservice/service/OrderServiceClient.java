package com.fooddelivery.deliveryservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fooddelivery.securitylib.config.FeignClientConfig;
import com.fooddelivery.shared.dto.DriverOrderDto;

@FeignClient(value = "order-service", url = "${order-service.url}", configuration = FeignClientConfig.class)
public interface OrderServiceClient {
    @GetMapping(path = "/internal/orders/driver/{orderId}", produces = "application/json")
    DriverOrderDto getDriverOrderById(@PathVariable Long orderId);
}
