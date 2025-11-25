package com.fooddelivery.userservice.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.fooddelivery.securitylib.config.FeignClientConfig;
import com.fooddelivery.shared.dto.RestaurantRequestDto;

@FeignClient(value = "restaurant-service", url = "${restaurant-service.url}", configuration = FeignClientConfig.class)
public interface RestaurantServiceClient {
    @PostMapping(path = "/internal/restaurants", consumes = "application/json", produces = "application/json")
    Map<String, Long> createRestaurant(RestaurantRequestDto requestDto);
}