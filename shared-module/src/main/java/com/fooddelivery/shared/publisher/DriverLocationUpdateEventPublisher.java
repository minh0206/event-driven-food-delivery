package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.DriverLocationUpdateEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DriverLocationUpdateEventPublisher {
    public static final String TOPIC = "driver_location_updates";
    private final KafkaTemplate<String, DriverLocationUpdateEvent> kafkaTemplate;

    public void publish(DriverLocationUpdateEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}