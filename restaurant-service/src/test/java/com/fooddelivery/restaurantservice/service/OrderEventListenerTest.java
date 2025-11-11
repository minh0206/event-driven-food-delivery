package com.fooddelivery.restaurantservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fooddelivery.restaurantservice.model.Restaurant;
import com.fooddelivery.restaurantservice.model.RestaurantOrder;
import com.fooddelivery.restaurantservice.repository.RestaurantOrderRepository;
import com.fooddelivery.shared.enumerate.OrderStatus;
import com.fooddelivery.shared.event.OrderDeliveredEvent;
import com.fooddelivery.shared.event.OrderInTransitEvent;
import com.fooddelivery.shared.event.OrderItemDetails;
import com.fooddelivery.shared.event.OrderPlacedEvent;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private WebSocketNotificationService webSocketNotificationService;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    void handleOrderPlacedEvent_createsOrderAndSendsNotification() {
        // Arrange
        OrderItemDetails item = new OrderItemDetails(1L, 10L, 2);
        OrderPlacedEvent event = new OrderPlacedEvent(100L, 5L, List.of(item));

        Restaurant restaurant = new Restaurant();
        restaurant.setId(5L);
        restaurant.setOwnerId(50L);

        when(restaurantService.getRestaurantById(5L)).thenReturn(restaurant);

        // Act
        orderEventListener.handleOrderPlacedEvent(event);

        // Assert
        verify(restaurantService).createRestaurantOrder(event);
        verify(restaurantService).getRestaurantById(5L);
        verify(webSocketNotificationService).sendOrderStatusUpdateNotification("50", OrderStatus.PENDING);
    }

    @Test
    void handleOrderInTransitEvent_updatesOrderStatusAndSendsNotification() {
        // Arrange
        OrderInTransitEvent event = new OrderInTransitEvent(100L, 20L, 200L);

        RestaurantOrder restaurantOrder = new RestaurantOrder();
        restaurantOrder.setOrderId(100L);
        restaurantOrder.setRestaurantId(5L);
        restaurantOrder.setLocalStatus(OrderStatus.READY_FOR_PICKUP);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(5L);
        restaurant.setOwnerId(50L);

        when(restaurantService.getRestaurantOrder(100L)).thenReturn(restaurantOrder);
        when(restaurantService.getRestaurantById(5L)).thenReturn(restaurant);
        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenReturn(restaurantOrder);

        // Act
        orderEventListener.handleOrderInTransitEvent(event);

        // Assert
        verify(restaurantService).getRestaurantOrder(100L);
        verify(restaurantOrderRepository).save(restaurantOrder);
        verify(restaurantService).getRestaurantById(5L);
        verify(webSocketNotificationService).sendOrderStatusUpdateNotification("50", OrderStatus.IN_TRANSIT);
    }

    @Test
    void handleOrderDeliveredEvent_updatesOrderStatusAndSendsNotification() {
        // Arrange
        OrderDeliveredEvent event = new OrderDeliveredEvent(100L, 20L);

        RestaurantOrder restaurantOrder = new RestaurantOrder();
        restaurantOrder.setOrderId(100L);
        restaurantOrder.setRestaurantId(5L);
        restaurantOrder.setLocalStatus(OrderStatus.IN_TRANSIT);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(5L);
        restaurant.setOwnerId(50L);

        when(restaurantService.getRestaurantOrder(100L)).thenReturn(restaurantOrder);
        when(restaurantService.getRestaurantById(5L)).thenReturn(restaurant);
        when(restaurantOrderRepository.save(any(RestaurantOrder.class))).thenReturn(restaurantOrder);

        // Act
        orderEventListener.handleOrderDeliveredEvent(event);

        // Assert
        verify(restaurantService).getRestaurantOrder(100L);
        verify(restaurantOrderRepository).save(restaurantOrder);
        verify(restaurantService).getRestaurantById(5L);
        verify(webSocketNotificationService).sendOrderStatusUpdateNotification("50", OrderStatus.DELIVERED);
    }
}
