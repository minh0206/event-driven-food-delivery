package com.fooddelivery.deliveryservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DriverService {
    private static final String DRIVER_NOT_FOUND = "Driver not found";
    private static final String TOPIC_DRIVER_LOCATION_UPDATE = "driver_location_updates";
    private DriverRepository driverRepository;
    private KafkaTemplate<String, DriverLocationUpdateEvent> kafkaTemplate;

    public Driver createDriver(Long userId) {
        Driver driver = new Driver();
        driver.setUserId(userId);
        driver.setStatus(DriverStatus.OFFLINE);
        return driverRepository.save(driver);
    }

    public String getDriverStatus(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));
        return driver.getStatus().toString();
    }

    public Driver updateDriverStatus(Long userId, DriverStatus newStatus) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        driver.setStatus(newStatus);
        return driverRepository.save(driver);
    }

    public void updateDriverLocation(Long userId, double latitude, double longitude) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        // Update driver location
        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        driverRepository.save(driver);

        // Publish the driver location update event
        DriverLocationUpdateEvent event = new DriverLocationUpdateEvent(
                driver.getCurrentOrderId(),
                driver.getId(),
                latitude,
                longitude);
        kafkaTemplate.send(TOPIC_DRIVER_LOCATION_UPDATE, event);
    }

}