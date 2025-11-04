package com.fooddelivery.deliveryservice.service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.OrderAcceptedEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderEventListener {
    private DriverRepository driverRepository;
    private DriverEventPublisher driverEventPublisher;

    @KafkaListener(topics = "order_accepted", groupId = "delivery-service-group")
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
        driverEventPublisher.publishDriverAssignedEvent(assignedEvent);
    }

}