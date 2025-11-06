package com.fooddelivery.deliveryservice.service;

import org.springframework.stereotype.Service;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.dto.DriverOrderDto;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;
import com.fooddelivery.shared.event.OrderDeliveredEvent;
import com.fooddelivery.shared.event.OrderInTransitEvent;
import com.fooddelivery.shared.publisher.DriverLocationUpdateEventPublisher;
import com.fooddelivery.shared.publisher.OrderDeliveredEventPublisher;
import com.fooddelivery.shared.publisher.OrderInTransitEventPublisher;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DriverService {
    private static final String DRIVER_NOT_FOUND = "Driver not found.";
    private static final String ORDER_NOT_FOUND = "Driver is not assigned to an active order.";
    private final DriverRepository driverRepository;
    private final OrderServiceClient orderServiceClient;
    private final DriverLocationUpdateEventPublisher driverLocationUpdateEventPublisher;
    private final OrderInTransitEventPublisher orderInTransitEventPublisher;
    private final OrderDeliveredEventPublisher orderDeliveredEventPublisher;

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
        driverLocationUpdateEventPublisher.publish(event);
    }

    public DriverOrderDto getDriverOrder(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        // CRITICAL CHECK: Ensure the driver has an active order.
        Long orderId = driver.getCurrentOrderId();
        if (orderId == null) {
            throw new EntityNotFoundException(ORDER_NOT_FOUND);
        }

        return orderServiceClient.getOrderById(orderId);
    }

    public void markOrderAsInTransit(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        // CRITICAL CHECK: Ensure the driver has an active order.
        Long orderId = driver.getCurrentOrderId();
        if (orderId == null) {
            throw new EntityNotFoundException(ORDER_NOT_FOUND);
        }

        // No state change is needed for the driver entity itself (status is still
        // ON_DELIVERY),
        // but we publish the event to notify the rest of the system.
        OrderInTransitEvent event = new OrderInTransitEvent(
                orderId,
                driver.getId(),
                driver.getUserId());

        orderInTransitEventPublisher.publish(event);
        log.info("Driver #{} marked order #{} as IN_TRANSIT.", driver.getId(), orderId);
    }

    @Transactional // Ensure DB update and event publish are atomic
    public void markOrderAsDelivered(Long userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND));

        Long orderId = driver.getCurrentOrderId();
        if (orderId == null) {
            throw new EntityNotFoundException(ORDER_NOT_FOUND);
        }

        // IMPORTANT: Reset the driver's state so they are available for the next
        // delivery.
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setCurrentOrderId(null);
        driverRepository.save(driver);

        // Publish the event to notify the system of completion.
        OrderDeliveredEvent event = new OrderDeliveredEvent(orderId, driver.getId());
        orderDeliveredEventPublisher.publish(event);
        log.info("Driver #{} marked order #{} as DELIVERED and is now available.", driver.getId(), orderId);
    }
}