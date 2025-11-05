package com.fooddelivery.deliveryservice.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderReadyEvent;
import com.fooddelivery.shared.publisher.DriverAssignedEventPublisher;
import com.fooddelivery.shared.publisher.OrderAcceptedEventPublisher;
import com.fooddelivery.shared.publisher.OrderReadyEventPublisher;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private final DriverRepository driverRepository;
    private final DriverAssignedEventPublisher driverAssignedEventPublisher;
    private final WebSocketNotificationService webSocketNotificationService;

    @KafkaListener(topics = OrderAcceptedEventPublisher.TOPIC, groupId = "delivery-service-group")
    @Transactional
    public void handleOrderAccepted(OrderAcceptedEvent event) {
        log.info("Order #{} accepted, finding available driver.", event.orderId());

        // Step 1: Find available drivers
        List<Driver> availableDrivers = driverRepository.findAllByStatus(DriverStatus.AVAILABLE);

        if (availableDrivers.isEmpty()) {
            log.warn("No available drivers for order #{}. Order will be re-queued later.", event.orderId());
            // In a real system, you would implement a retry or alert mechanism here.
            return;
        }

        // Step 2: Select the best driver (for now, the first one)
        // A real implementation would calculate distance from
        // event.restaurantLatitude/Longitude
        Driver assignedDriver = availableDrivers.getFirst();

        // Step 3: Update driver's status and assign the order
        assignedDriver.setStatus(DriverStatus.ON_DELIVERY);
        assignedDriver.setCurrentOrderId(event.orderId());
        driverRepository.save(assignedDriver);

        log.info("Driver #{} (User ID: {}) assigned to order #{}.", assignedDriver.getId(), assignedDriver.getUserId(),
                event.orderId());

        // Step 4: Publish the DriverAssignedEvent
        DriverAssignedEvent assignedEvent = new DriverAssignedEvent(
                event.orderId(),
                assignedDriver.getId(),
                assignedDriver.getUserId());
        driverAssignedEventPublisher.publish(assignedEvent);

        // Step 5: Send WS notification to the driver
        webSocketNotificationService.sendOrderStatusUpdate(
                assignedDriver.getUserId().toString(),
                OrderStatus.DRIVER_ASSIGNED);
    }

    @KafkaListener(topics = OrderReadyEventPublisher.TOPIC, groupId = "delivery-service-group")
    public void handleOrderReadyEvent(OrderReadyEvent event) {
        // CRITICAL CHECK: Ensure the driver has an active order.
        Driver driver = driverRepository.findByCurrentOrderId(event.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for order ID: " + event.orderId()));

        // Send WS notification to the driver
        webSocketNotificationService.sendOrderStatusUpdate(
                driver.getUserId().toString(),
                OrderStatus.READY_FOR_PICKUP);
    }
}