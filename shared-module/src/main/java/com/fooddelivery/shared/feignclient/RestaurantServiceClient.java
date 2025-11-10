package com.fooddelivery.shared.feignclient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;
import com.fooddelivery.shared.dto.RestaurantRequestDto;

@FeignClient(name = "restaurant-service", configuration = FeignClientConfiguration.class)
public interface RestaurantServiceClient {
    @PostMapping(path = "/internal/restaurants", consumes = "application/json", produces = "application/json")
    Map<String, Long> createRestaurant(RestaurantRequestDto requestDto);
}