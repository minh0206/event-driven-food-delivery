package com.fooddelivery.deliveryservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fooddelivery.shared.event.DriverAssignedEvent;
import com.fooddelivery.shared.event.DriverLocationUpdateEvent;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DriverEventPublisher {
    private static final String TOPIC_DRIVER_ASSIGNED = "driver_assigned";
    private static final String TOPIC_DRIVER_LOCATION_UPDATE = "driver_location_updates";
    private final KafkaTemplate<String, DriverAssignedEvent> assignedKafkaTemplate;
    private final KafkaTemplate<String, DriverLocationUpdateEvent> locationUpdateKafkaTemplate;

    public void publishDriverAssignedEvent(DriverAssignedEvent event) {
        assignedKafkaTemplate.send(TOPIC_DRIVER_ASSIGNED, event);
    }

    public void publishDriverLocationUpdateEvent(DriverLocationUpdateEvent event) {
        locationUpdateKafkaTemplate.send(TOPIC_DRIVER_LOCATION_UPDATE, event);
    }
}
