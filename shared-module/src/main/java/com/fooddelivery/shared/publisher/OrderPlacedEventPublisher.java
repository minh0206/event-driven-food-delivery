package com.fooddelivery.shared.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.shared.event.OrderPlacedEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderPlacedEventPublisher {
    public static final String TOPIC = "order_placed";
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publish(OrderPlacedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
