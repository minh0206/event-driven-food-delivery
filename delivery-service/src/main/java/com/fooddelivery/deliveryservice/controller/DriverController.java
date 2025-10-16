package com.fooddelivery.deliveryservice.controller;

import com.fooddelivery.deliveryservice.dto.LocationUpdateRequestDto;
import com.fooddelivery.deliveryservice.dto.UpdateStatusRequestDto;
import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PutMapping("/me/status")
    // @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public ResponseEntity<Driver> updateStatus(
            @RequestBody @Valid UpdateStatusRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Driver updatedDriver = driverService.updateDriverStatus(userId, request.newStatus());
        return ResponseEntity.ok(updatedDriver);
    }

    @PostMapping("/me/location")
    // @PreAuthorize("hasRole('DELIVERY_DRIVER')")
    public ResponseEntity<Void> updateLocation(
            @RequestBody @Valid LocationUpdateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        driverService.updateDriverLocation(userId, request.latitude(), request.longitude());
        return ResponseEntity.ok().build();
    }
}