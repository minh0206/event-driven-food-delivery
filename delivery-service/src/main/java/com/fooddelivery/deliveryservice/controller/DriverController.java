package com.fooddelivery.deliveryservice.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.deliveryservice.dto.LocationUpdateRequestDto;
import com.fooddelivery.deliveryservice.dto.UpdateStatusRequestDto;
import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.service.DriverService;
import com.fooddelivery.shared.dto.DriverOrderDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@AllArgsConstructor
public class DriverController {
    private DriverService driverService;

    @GetMapping("/status")
    public Map<String, String> getDriverStatus(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return Map.of("status", driverService.getDriverStatus(userId));
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public Map<String, String> updateStatus(
            @RequestBody @Valid UpdateStatusRequestDto requestDto,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Driver updatedDriver = driverService.updateDriverStatus(userId, requestDto.status());
        return Map.of("status", updatedDriver.getStatus().toString());
    }

    @GetMapping("/order")
    public DriverOrderDto getDriverOrder(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        return driverService.getDriverOrder(userId);
    }

    @PostMapping("/order/pickup")
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public ResponseEntity<Void> markOrderAsInTransit(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        driverService.markOrderAsInTransit(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/complete")
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public ResponseEntity<Void> markOrderAsCompleted(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        driverService.markOrderAsDelivered(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/location")
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public ResponseEntity<Void> updateLocation(
            @RequestBody @Valid LocationUpdateRequestDto requestDto,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        driverService.updateDriverLocation(userId, requestDto.latitude(), requestDto.longitude());
        return ResponseEntity.ok().build();
    }
}