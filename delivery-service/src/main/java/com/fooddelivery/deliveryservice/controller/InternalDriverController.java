package com.fooddelivery.deliveryservice.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.service.DriverService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/internal/drivers")
@AllArgsConstructor
public class InternalDriverController {
    private DriverService driverService;

    @PostMapping()
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public Map<String, Long> createDriver(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Driver createdDriver = driverService.createDriver(userId);
        return Map.of("driverId", createdDriver.getId());
    }
}