package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderReadyEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderReadyEventPublisher {
    public static final String TOPIC = "order_ready";
    private final KafkaTemplate<String, OrderReadyEvent> kafkaTemplate;

    public void publish(OrderReadyEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}