package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.model.Driver;
import com.fooddelivery.deliveryservice.model.DriverStatus;
import com.fooddelivery.deliveryservice.repository.DriverRepository;
import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DeliveryAssignmentService {
    private static final String TOPIC_DRIVER_ASSIGNED = "driver_assigned";

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private KafkaTemplate<String, DriverAssignedEvent> kafkaTemplate;

    @KafkaListener(topics = "order_accepted", groupId = "delivery-group")
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
        // A real implementation would calculate distance from event.restaurantLatitude/Longitude
        Driver assignedDriver = availableDrivers.getFirst();

        // Step 3: Update driver's status and assign the order
        assignedDriver.setStatus(DriverStatus.ON_DELIVERY);
        assignedDriver.setCurrentOrderId(event.orderId());
        driverRepository.save(assignedDriver);

        log.info("Driver #{} (User ID: {}) assigned to order #{}.", assignedDriver.getId(), assignedDriver.getUserId(), event.orderId());

        // Step 4: Publish the DriverAssignedEvent
        DriverAssignedEvent assignedEvent = new DriverAssignedEvent(
                event.orderId(),
                assignedDriver.getId(),
                assignedDriver.getUserId()
        );
        kafkaTemplate.send(TOPIC_DRIVER_ASSIGNED, assignedEvent);
    }
}