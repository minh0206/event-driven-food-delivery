package com.fooddelivery.shared.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;
import com.fooddelivery.shared.dto.DriverOrderDto;

@FeignClient(name = "order-service", configuration = FeignClientConfiguration.class)
public interface OrderServiceClient {
    @GetMapping(path = "/internal/orders/{orderId}", produces = "application/json")
    DriverOrderDto getOrderById(@PathVariable Long orderId);
}
