package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.DriverAssignedEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DriverAssignedEventPublisher {
    public static final String TOPIC = "driver_assigned";
    private final KafkaTemplate<String, DriverAssignedEvent> kafkaTemplate;

    public void publish(DriverAssignedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}