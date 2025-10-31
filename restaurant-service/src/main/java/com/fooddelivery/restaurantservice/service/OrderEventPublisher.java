package com.fooddelivery.restaurantservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.shared.event.OrderAcceptedEvent;
import com.fooddelivery.shared.event.OrderStatusUpdateEvent;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderEventPublisher {
    private static final String TOPIC_ORDER_STATUS = "order_status_updates";
    private static final String TOPIC_ORDER_ACCEPTED = "order_accepted";

    private final KafkaTemplate<String, OrderStatusUpdateEvent> orderStatusUpdateTemplate;
    private final KafkaTemplate<String, OrderAcceptedEvent> orderAcceptedTemplate;

    public void publishOrderStatusUpdate(RestaurantOrder order) {
        OrderStatusUpdateEvent statusUpdateEvent = new OrderStatusUpdateEvent(order.getOrderId(),
                order.getLocalStatus());
        orderStatusUpdateTemplate.send(TOPIC_ORDER_STATUS, statusUpdateEvent);
    }

    public void publishOrderAccepted(RestaurantOrder order, Restaurant restaurant) {
        // TODO: Implement restaurant latitude and longitude
        OrderAcceptedEvent acceptedEvent = new OrderAcceptedEvent(
                order.getOrderId(),
                restaurant.getId(),
                1F,
                1F);
        orderAcceptedTemplate.send(TOPIC_ORDER_ACCEPTED, acceptedEvent);
    }
}
