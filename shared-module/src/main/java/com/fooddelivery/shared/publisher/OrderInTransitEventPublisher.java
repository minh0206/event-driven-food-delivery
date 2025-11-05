package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderInTransitEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderInTransitEventPublisher {
    public static final String TOPIC = "order_in_transit";
    private final KafkaTemplate<String, OrderInTransitEvent> kafkaTemplate;

    public void publish(OrderInTransitEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}