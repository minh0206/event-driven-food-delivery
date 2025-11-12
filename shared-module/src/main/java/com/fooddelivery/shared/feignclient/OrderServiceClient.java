package com.fooddelivery.shared.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fooddelivery.securitylib.config.FeignClientConfiguration;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.dto.MasterOrderDto;

@FeignClient(name = "order-service", configuration = FeignClientConfiguration.class)
public interface OrderServiceClient {
    @GetMapping(path = "/internal/orders/driver/{orderId}", produces = "application/json")
    DriverOrderDto getDriverOrderById(@PathVariable Long orderId);

    @GetMapping(path = "/internal/orders/restaurant/{restaurantId}", produces = "application/json")
    Page<MasterOrderDto> getHistoricalOrdersByRestaurantId(
            @PathVariable Long restaurantId, Pageable pageable);
}
