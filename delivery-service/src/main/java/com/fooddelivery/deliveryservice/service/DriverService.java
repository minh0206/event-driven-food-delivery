package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DriverService {
    private static final String TOPIC_DRIVER_LOCATION_UPDATE = "driver_location_updates";

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private KafkaTemplate<String, DriverLocationUpdateEvent> kafkaTemplate;

    public Driver updateDriverStatus(Long userId, DriverStatus newStatus) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Driver profile not found for user: " + userId));

        driver.setStatus(newStatus);
        return driverRepository.save(driver);
    }

    public void updateDriverLocation(Long userId, double latitude, double longitude) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        // Update driver location
        driver.setCurrentLatitude(latitude);
        driver.setCurrentLongitude(longitude);
        driverRepository.save(driver);

        // Publish the driver location update event
        DriverLocationUpdateEvent event = new DriverLocationUpdateEvent(
                driver.getCurrentOrderId(),
                driver.getId(),
                latitude,
                longitude
        );
        kafkaTemplate.send(TOPIC_DRIVER_LOCATION_UPDATE, event);

    }
}