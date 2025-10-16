package com.fooddelivery.deliveryservice.repository;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUserId(Long userId);

    // Find all drivers who are online and not on a delivery
    List<Driver> findAllByStatus(DriverStatus status);
}