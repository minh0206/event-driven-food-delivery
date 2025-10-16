package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public Driver updateDriverStatus(Long userId, DriverStatus newStatus) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Driver profile not found for user: " + userId));

        driver.setStatus(newStatus);
        return driverRepository.save(driver);
    }
}