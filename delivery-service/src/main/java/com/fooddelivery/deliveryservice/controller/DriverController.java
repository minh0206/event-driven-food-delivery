package com.fooddelivery.deliveryservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.deliveryservice.dto.DriverDto;
import com.fooddelivery.deliveryservice.dto.LocationUpdateRequestDto;
import com.fooddelivery.deliveryservice.dto.UpdateStatusRequestDto;
import com.fooddelivery.deliveryservice.mapper.DriverMapper;
import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.service.DriverService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@AllArgsConstructor
public class DriverController {
    private DriverService driverService;
    private DriverMapper driverMapper;

    @PutMapping("/status")
    @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public DriverDto updateStatus(
            @RequestBody @Valid UpdateStatusRequestDto requestDto,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Driver updatedDriver = driverService.updateDriverStatus(userId, requestDto.status());
        return driverMapper.toDto(updatedDriver);
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