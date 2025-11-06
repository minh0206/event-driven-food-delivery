package com.fooddelivery.userservice.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;
import com.fooddelivery.userservice.dto.RestaurantRegisterRequestDto;

@FeignClient(name = "restaurant-service", url = "${restaurant-service.url}", configuration = FeignClientConfiguration.class)
public interface RestaurantServiceClient {
    @PostMapping(path = "/internal/restaurants", consumes = "application/json", produces = "application/json")
    Map<String, Long> createRestaurant(RestaurantRegisterRequestDto requestDto);
}