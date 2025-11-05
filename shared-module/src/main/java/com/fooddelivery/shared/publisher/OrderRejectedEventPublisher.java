package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderRejectedEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderRejectedEventPublisher {
    public static final String TOPIC = "order_rejected";
    private final KafkaTemplate<String, OrderRejectedEvent> kafkaTemplate;

    public void publish(OrderRejectedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}